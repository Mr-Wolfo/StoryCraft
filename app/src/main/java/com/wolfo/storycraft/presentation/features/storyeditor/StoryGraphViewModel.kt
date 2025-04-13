package com.wolfo.storycraft.presentation.features.storyeditor

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.System.out


// Placeholder для ваших Use Cases и Repository
class GetStoryGraphDataUseCase() { // Замените на реальный Use Case
    suspend operator fun invoke(storyId: Long): Result<Pair<Map<String, GraphNode>, List<GraphEdge>>> {return error("")
    }
}
class SaveStoryGraphLayoutUseCase() { // Замените на реальный Use Case
    suspend operator fun invoke(storyId: Long, nodes: Map<String, GraphNode>): Result<Unit> { return Result.success(Unit) }
}
class CreateEdgeUseCase { // Замените на реальный Use Case
    suspend operator fun invoke(storyId: Long, edge: GraphEdge): Result<GraphEdge> {return Result.success(GraphEdge(id = "5", startNodeId = "3", endNodeId = "4", startChoiceIndex = 0))
    } // Возвращает созданное ребро с ID
}
interface DeleteEdgeUseCase { // Замените на реальный Use Case
    suspend operator fun invoke(edgeId: String): Result<Unit>
}

class StoryGraphViewModel(
    private val savedStateHandle: SavedStateHandle, // Для получения ID истории из навигации
    private val getStoryGraphDataUseCase: GetStoryGraphDataUseCase, // Зависимости внедряются через Koin/Hilt
    private val saveStoryGraphLayoutUseCase: SaveStoryGraphLayoutUseCase,
    private val createEdgeUseCase: CreateEdgeUseCase,
    private val deleteEdgeUseCase: DeleteEdgeUseCase
    // Добавьте другие Use Cases по мере необходимости (удаление узла, обновление текста и т.д.)
) : ViewModel() {

    private val storyId: Long = savedStateHandle.get<Long>("storyId") ?: error("storyId not found in navigation args")

    private val _uiState = MutableStateFlow(StoryGraphUiState(isLoading = true))
    val uiState: StateFlow<StoryGraphUiState> = _uiState.asStateFlow()

    init {
        loadInitialGraph()
    }

    private fun loadInitialGraph() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getStoryGraphDataUseCase(storyId)
            result.onSuccess { (nodes, edges) ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        nodes = nodes,
                        edges = edges,
                        storyTitle = "История $storyId" // Загрузить реальное название
                        // TODO: Загрузить сохраненные canvasOffset и canvasScale, если есть
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = "Ошибка загрузки графа: ${error.localizedMessage}")
                }
            }
        }
    }

    // --- Обработчики событий от UI ---

    fun onCanvasPan(dragAmount: Offset) {
        _uiState.update {
            it.copy(canvasOffset = it.canvasOffset + dragAmount / it.canvasScale) // Учитываем масштаб
        }
    }

    fun onCanvasZoom(zoomChange: Float, centroid: Offset) {
        val currentScale = _uiState.value.canvasScale
        val newScale = (currentScale * zoomChange).coerceIn(0.1f, 5f) // Ограничиваем масштаб

        // Корректируем смещение, чтобы зум происходил относительно точки centroid
        val currentOffset = _uiState.value.canvasOffset
        // Преобразуем centroid из экранных координат в координаты холста до зума
        val pointOnCanvas = (centroid / currentScale) - currentOffset
        // Новое смещение = -(точка на холсте - точка на экране после зума)
        val newOffset = -(pointOnCanvas - (centroid / newScale))

        _uiState.update {
            it.copy(
                canvasScale = newScale,
                canvasOffset = newOffset
            )
        }
    }

    fun onNodeDragStart(nodeId: String) {
        _uiState.update { it.copy(draggingNodeId = nodeId) }
    }

    fun onNodeDrag(nodeId: String, dragAmount: Offset) {
        _uiState.update { currentState ->
            val currentNodes = currentState.nodes
            val nodeToUpdate = currentNodes[nodeId]
            if (nodeToUpdate != null && currentState.draggingNodeId == nodeId) {
                val newPosition = nodeToUpdate.position + dragAmount / currentState.canvasScale // Учитываем масштаб
                val updatedNode = nodeToUpdate.copy(position = newPosition)
                currentState.copy(nodes = currentNodes + (nodeId to updatedNode))
            } else {
                currentState // Без изменений, если узел не найден или не перетаскивается
            }
        }
    }

    fun onNodeDragEnd(nodeId: String) {
        if (_uiState.value.draggingNodeId == nodeId) {
            _uiState.update { it.copy(draggingNodeId = null) }
            // TODO: Возможно, здесь стоит инициировать сохранение layout'а (опционально, можно по кнопке)
            // saveLayout()
        }
    }

    fun onNodeSelect(nodeId: String?) {
        _uiState.update { it.copy(selectedNodeId = nodeId) }
    }

    fun onEdgeDragStart(nodeId: String, choiceIndex: Int, startPosition: Offset) {
        // Начальная позиция для отрисовки временной линии
        _uiState.update { it.copy(dragNewEdgeInfo = DragNewEdgeInfo(nodeId, choiceIndex, startPosition)) }
    }

    fun onEdgeDrag(currentPosition: Offset) {
        _uiState.update { currentState ->
            currentState.dragNewEdgeInfo?.let { dragInfo ->
                currentState.copy(dragNewEdgeInfo = dragInfo.copy(dragEndPosition = currentPosition))
            } ?: currentState // Без изменений, если не в режиме перетаскивания ребра
        }
    }

    fun onEdgeDragEnd(endNodeId: String?) {
        val dragInfo = _uiState.value.dragNewEdgeInfo ?: return // Выходим, если не было перетаскивания
        _uiState.update { it.copy(dragNewEdgeInfo = null) } // Завершаем режим перетаскивания

        if (endNodeId != null && endNodeId != dragInfo.startNodeId) {
            // Пытаемся создать новое ребро
            val newEdge = GraphEdge(
                id = "temp_${System.currentTimeMillis()}", // Временный ID, сервер/БД присвоит реальный
                startNodeId = dragInfo.startNodeId,
                endNodeId = endNodeId,
                startChoiceIndex = dragInfo.startChoiceIndex
            )
            // TODO: Проверить, нет ли уже ребра от этого choiceIndex
            // TODO: Возможно, нужно удалить старое ребро, если оно было для этого choiceIndex

            viewModelScope.launch {
                val result = createEdgeUseCase(storyId, newEdge)
                result.onSuccess { createdEdge ->
                    _uiState.update { currentState ->
                        currentState.copy(edges = currentState.edges + createdEdge)
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = "Не удалось создать связь: ${error.localizedMessage}") }
                }
            }
        }
    }

    // TODO: Метод для удаления ребра (например, по долгому нажатию или через контекстное меню)
    fun deleteEdge(edgeId: String) {
        viewModelScope.launch {
            val result = deleteEdgeUseCase(edgeId)
            result.onSuccess {
                _uiState.update { currentState ->
                    currentState.copy(edges = currentState.edges.filterNot { it.id == edgeId })
                }
            }.onFailure { /* Обработка ошибки */ }
        }
    }


    // Метод для сохранения текущего расположения узлов
    fun saveLayout() {
        viewModelScope.launch {
            // TODO: Показать индикатор сохранения
            val result = saveStoryGraphLayoutUseCase(storyId, _uiState.value.nodes)
            result.onSuccess { /* Скрыть индикатор */ }
            result.onFailure { error ->
                _uiState.update { it.copy(error = "Ошибка сохранения расположения: ${error.localizedMessage}") }
            }
        }
    }

}


data class GraphNode(
    val id: String, // Используем String для гибкости, будет связано с Long ID из БД
    val previewText: String,
    var position: Offset, // var, чтобы можно было изменять при перетаскивании
    val size: Size = Size(200f, 120f), // Примерный фиксированный размер узла
    val isStartNode: Boolean = false,
    val isEndNode: Boolean = false,
    // --- Относительные смещения точек подключения ---
    // Точка входа (например, центр левой грани)
    val entryPointOffset: Offset = Offset(0f, size.height / 2f),
    // Точки выхода для 1, 2, 3 выборов (например, на правой грани)
    val choicePointOffsets: List<Offset> = listOf(
        Offset(size.width, size.height * 0.25f),
        Offset(size.width, size.height * 0.50f),
        Offset(size.width, size.height * 0.75f)
    )
)

/**
 * Представляет ребро графа (связь от выбора к целевой странице).
 * @param id Уникальный идентификатор ребра (вероятно, совпадает с Choice.id).
 * @param startNodeId ID узла-источника.
 * @param endNodeId ID узла-цели.
 * @param startChoiceIndex Индекс точки выхода на узле-источнике (0, 1 или 2).
 */
data class GraphEdge(
    val id: String, // Используем String, будет связано с Long ID из БД
    val startNodeId: String,
    val endNodeId: String,
    val startChoiceIndex: Int // 0, 1 или 2
)

/**
 * Состояние текущего процесса создания новой связи (ребра).
 * @param startNodeId ID узла, с которого начали тянуть связь.
 * @param startChoiceIndex Индекс точки выхода, с которой начали тянуть.
 * @param dragEndPosition Текущая позиция пальца/курсора во время перетаскивания.
 */
data class DragNewEdgeInfo(
    val startNodeId: String,
    val startChoiceIndex: Int,
    val dragEndPosition: Offset
)

/**
 * Полное состояние UI для экрана редактора графа историй.
 */
data class StoryGraphUiState(
    // --- Структура графа ---
    val nodes: Map<String, GraphNode> = emptyMap(), // Узлы (страницы), используем Map для быстрого доступа по ID
    val edges: List<GraphEdge> = emptyList(),      // Ребра (связи/выборы)

    // --- Состояние холста ---
    val canvasOffset: Offset = Offset.Zero, // Смещение холста (для панорамирования)
    val canvasScale: Float = 1f,            // Масштаб холста (для зума)

    // --- Состояние взаимодействия ---
    val selectedNodeId: String? = null,         // ID выделенного узла (для подсветки, редактирования)
    val draggingNodeId: String? = null,       // ID узла, который сейчас перетаскивают
    val dragNewEdgeInfo: DragNewEdgeInfo? = null, // Информация о перетаскивании новой связи

    // --- Метаданные и статус ---
    val storyTitle: String = "Новая история", // Название редактируемой истории
    val isLoading: Boolean = false,
    val error: String? = null
)
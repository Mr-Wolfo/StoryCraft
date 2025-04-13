package com.wolfo.storycraft.presentation.features.storyeditor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoryEditorScreen(storyId: Long?) {
    MyScreen()
}


@OptIn(ExperimentalTextApi::class)
@Composable
fun StoryGraphEditor(
    // viewModel: StoryGraphViewModel = koinViewModel() // Получаем ViewModel через DI
    viewModel: StoryGraphViewModel = koinViewModel()// Пока передаем явно для примера
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val textMeasurer = rememberTextMeasurer() // Для измерения текста на Canvas

    Box(modifier = Modifier.fillMaxSize()) {
        // Основной холст для графа
        GraphCanvas(
            modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(alpha = 0.8f)),
            uiState = uiState,
            textMeasurer = textMeasurer,
            onPan = viewModel::onCanvasPan,
            onZoom = viewModel::onCanvasZoom,
            onNodeClick = viewModel::onNodeSelect,
            onNodeDragStart = viewModel::onNodeDragStart,
            onNodeDrag = viewModel::onNodeDrag,
            onNodeDragEnd = viewModel::onNodeDragEnd,
            onEdgeDragStart = viewModel::onEdgeDragStart,
            onEdgeDrag = viewModel::onEdgeDrag,
            onEdgeDragEnd = viewModel::onEdgeDragEnd
        )

        // Элементы поверх холста (кнопки, статус)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("История: ${uiState.storyTitle}", style = MaterialTheme.typography.titleMedium)
            Text("Масштаб: ${"%.2f".format(uiState.canvasScale)}", style = MaterialTheme.typography.bodySmall)
            Text("Смещение: (${"%.1f".format(uiState.canvasOffset.x)}, ${"%.1f".format(uiState.canvasOffset.y)})", style = MaterialTheme.typography.bodySmall)
            // TODO: Добавить кнопки управления (Save, Center, Zoom +/-)
            Button(onClick = { viewModel.saveLayout() }) { Text("Сохранить") }
        }

        // Индикатор загрузки
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // Отображение ошибок (например, в Snackbar)
        uiState.error?.let { errorMsg ->
            // TODO: Показать Snackbar или другое сообщение об ошибке
            LaunchedEffect(errorMsg) {
                // snackbarHostState.showSnackbar(message = errorMsg)
                println("Ошибка: $errorMsg") // Пока просто выводим в консоль
                // Сбросить ошибку после показа? viewModel.clearError()
            }
        }

        // TODO: Добавить SnackbarHost для отображения ошибок/уведомлений
        // val snackbarHostState = remember { SnackbarHostState() }
        // SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun GraphCanvas(
    modifier: Modifier = Modifier,
    uiState: StoryGraphUiState,
    textMeasurer: TextMeasurer,
    onPan: (Offset) -> Unit,
    onZoom: (zoomChange: Float, centroid: Offset) -> Unit,
    onNodeClick: (nodeId: String?) -> Unit,
    onNodeDragStart: (nodeId: String) -> Unit,
    onNodeDrag: (nodeId: String, dragAmount: Offset) -> Unit,
    onNodeDragEnd: (nodeId: String) -> Unit,
    onEdgeDragStart: (nodeId: String, choiceIndex: Int, startPosition: Offset) -> Unit,
    onEdgeDrag: (currentPosition: Offset) -> Unit,
    onEdgeDragEnd: (endNodeId: String?) -> Unit,
) {
    val density = LocalDensity.current

    // -- Переменные для обработки жестов --
    var dragNodeId: String? = null
    var dragEdgeStartInfo: Triple<String, Int, Offset>? = null // nodeId, choiceIndex, startOffset

    Canvas(
        modifier = modifier.pointerInput(Unit) { // Unit - ключ для перезапуска, если нужно
            detectTransformGestures( // Обработка зума и панорамирования (если нет перетаскивания узла/ребра)
                onGesture = { centroid, pan, zoom, _ ->
                    if (dragNodeId == null && dragEdgeStartInfo == null) { // Только если не тащим узел или ребро
                        onZoom(zoom, centroid) // Зум относительно точки касания
                        onPan(pan) // Панорамирование
                    }
                }
            )
        }
            .pointerInput(uiState.nodes, uiState.canvasScale, uiState.canvasOffset) { // Перезапускаем при изменении узлов/трансформаций
                // Более сложная обработка для перетаскивания узлов и начала перетаскивания ребер
                forEachGesture {
                    awaitPointerEventScope {
                        val down = awaitFirstDown(requireUnconsumed = false) // Ждем первого нажатия

                        val downPositionCanvas = screenToCanvas(down.position, uiState.canvasOffset, uiState.canvasScale)
                        val (hitNodeId, hitChoiceIndex) = findHitTarget(downPositionCanvas, uiState.nodes)

                        // --- Начало перетаскивания ребра ---
                        if (hitNodeId != null && hitChoiceIndex != null) {
                            val node = uiState.nodes[hitNodeId]!!
                            val choicePointOffset = node.choicePointOffsets.getOrNull(hitChoiceIndex)
                            if (choicePointOffset != null) {
                                val startPos = node.position + choicePointOffset // Позиция точки на холсте
                                dragEdgeStartInfo = Triple(hitNodeId, hitChoiceIndex, startPos)
                                val startPosScreen = canvasToScreen(startPos, uiState.canvasOffset, uiState.canvasScale) // Начальная точка для ViewModel
                                onEdgeDragStart(hitNodeId, hitChoiceIndex, startPosScreen)

                                // Цикл перетаскивания ребра
                                drag(down.id) { change ->
                                    onEdgeDrag(change.position) // Передаем текущую позицию пальца/курсора (экранные координаты)
                                    change.consume()
                                }

                                // Завершение перетаскивания ребра
                                val finalPositionCanvas = screenToCanvas(currentEvent.changes.first().position, uiState.canvasOffset, uiState.canvasScale)
                                val (endNodeHitId, _) = findHitTarget(finalPositionCanvas, uiState.nodes, checkOnlyEntry = true) // Ищем только попадание в узел (вход)
                                onEdgeDragEnd(endNodeHitId)
                                dragEdgeStartInfo = null // Сбрасываем состояние
                            }
                        }
                        // --- Начало перетаскивания узла ---
                        else if (hitNodeId != null) {
                            dragNodeId = hitNodeId
                            onNodeDragStart(hitNodeId)
                            // Цикл перетаскивания узла
                            drag(down.id) { change ->
                                onNodeDrag(hitNodeId, change.positionChange()) // Передаем смещение
                                change.consume()
                            }
                            // Завершение перетаскивания узла
                            onNodeDragEnd(hitNodeId)
                            dragNodeId = null // Сбрасываем состояние

                        }
                        // --- Обработка клика (если не было перетаскивания) ---
                        else {
                            // Если было короткое нажатие без перемещения - считаем это кликом
                            val tap = awaitTouchSlopOrCancellation(down.id) { change, over ->
                                // Если палец сместился достаточно далеко или прошло время - отменяем тап
                                // change.consume()
                            }
                            if (tap != null) {
                                // Клик по пустому месту - сбросить выделение
                                onNodeClick(null)
                                tap.consume()
                            } else {
                                // Возможно, был скролл или зум, не считаем кликом
                            }
                        }
                    }
                }
            }
    ) { // this: DrawScope
        // 1. Применяем трансформации холста (сдвиг и масштаб)
        translate(left = uiState.canvasOffset.x * uiState.canvasScale, top = uiState.canvasOffset.y * uiState.canvasScale) {
            scale(scale = uiState.canvasScale, pivot = Offset.Zero) { // Масштабируем относительно (0,0) после сдвига

                // 2. Рисуем ребра (Edges)
                uiState.edges.forEach { edge ->
                    val startNode = uiState.nodes[edge.startNodeId]
                    val endNode = uiState.nodes[edge.endNodeId]
                    if (startNode != null && endNode != null) {
                        drawEdge(startNode, endNode, edge.startChoiceIndex)
                    }
                }

                // 3. Рисуем узлы (Nodes)
                uiState.nodes.values.forEach { node ->
                    drawNode(
                        node = node,
                        isSelected = node.id == uiState.selectedNodeId,
                        isDragging = node.id == uiState.draggingNodeId,
                        textMeasurer = textMeasurer
                    )
                }

                // 4. Рисуем перетаскиваемое новое ребро
                uiState.dragNewEdgeInfo?.let { dragInfo ->
                    val startNode = uiState.nodes[dragInfo.startNodeId]
                    if (startNode != null) {
                        val startPoint = startNode.position + startNode.choicePointOffsets[dragInfo.startChoiceIndex]
                        // Преобразуем экранные координаты конца перетаскивания в координаты холста
                        val endPointOnCanvas = screenToCanvas(dragInfo.dragEndPosition, uiState.canvasOffset, uiState.canvasScale)
                        drawNewEdgeLine(startPoint, endPointOnCanvas)
                    }
                }
            } // scale
        } // translate
    }
}

// --- Вспомогательные функции ---

// Преобразование координат: Экран -> Холст
fun screenToCanvas(screenPos: Offset, canvasOffset: Offset, canvasScale: Float): Offset {
    return (screenPos / canvasScale) - canvasOffset
}

// Преобразование координат: Холст -> Экран
fun canvasToScreen(canvasPos: Offset, canvasOffset: Offset, canvasScale: Float): Offset {
    return (canvasPos + canvasOffset) * canvasScale
}


// Функция определения, попал ли клик/тап в узел или точку выбора
// Возвращает Pair<NodeID?, ChoiceIndex?>
fun findHitTarget(
    canvasPosition: Offset,
    nodes: Map<String, GraphNode>,
    hitSlop: Float = 20f, // Допуск попадания (в пикселях холста)
    checkOnlyEntry: Boolean = false // Искать только попадание в область узла (для конца ребра)
): Pair<String?, Int?> {
    nodes.values.forEach { node ->
        // Проверка попадания в точку выбора (выход)
        if (!checkOnlyEntry) {
            node.choicePointOffsets.forEachIndexed { index, offset ->
                val choicePointPos = node.position + offset
                if ((canvasPosition - choicePointPos).getDistanceSquared() < hitSlop * hitSlop) {
                    return Pair(node.id, index) // Попали в точку выбора
                }
            }
        }

        // Проверка попадания в область узла
        val nodeRect = androidx.compose.ui.geometry.Rect(node.position, node.size)
        // Немного увеличим область для удобства попадания
        val inflatedRect = nodeRect.inflate(hitSlop / 2)
        if (inflatedRect.contains(canvasPosition)) {
            if (checkOnlyEntry) {
                // Для конца ребра достаточно попадания в узел
                return Pair(node.id, null)
            } else {
                // Проверяем, не ближе ли к точке входа? (не обязательно, но можно)
                // val entryPointPos = node.position + node.entryPointOffset
                return Pair(node.id, null) // Попали в узел (не в точку выбора)
            }
        }
    }
    return Pair(null, null) // Никуда не попали
}


// --- Функции отрисовки (внутри DrawScope) ---

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawNode(
    node: GraphNode,
    isSelected: Boolean,
    isDragging: Boolean,
    textMeasurer: TextMeasurer
) {
    val nodeSize = node.size
    val nodePosition = node.position
    val borderColor = when {
        isSelected -> Color.Blue
        node.isStartNode -> Color(0xFF4CAF50) // Зеленый для старта
        else -> Color.Black
    }
    val borderWidth = if (isSelected || isDragging) 6f else 3f
    val backgroundColor = if (node.isEndNode) Color.Gray.copy(alpha = 0.5f) else Color.White

    // Рисуем фон и обводку узла
    drawRoundRect(
        color = backgroundColor,
        topLeft = nodePosition,
        size = nodeSize,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
    )
    drawRoundRect(
        color = borderColor,
        topLeft = nodePosition,
        size = nodeSize,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
        style = Stroke(width = borderWidth)
    )

    // Рисуем текст внутри узла
    val textStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp // Пример
    )
    val measuredText = textMeasurer.measure(
        text = AnnotatedString(node.previewText), // Берем previewText
        style = textStyle,
        constraints = Constraints(maxWidth = (nodeSize.width * 0.9f).toInt()) // Ограничиваем ширину
    )
    drawText(
        textLayoutResult = measuredText,
        topLeft = nodePosition + Offset(nodeSize.width * 0.05f, nodeSize.height * 0.1f) // Отступы
    )

    // Рисуем точки входа/выхода
    val pointRadius = 8f
    // Точка входа (слева)
    drawCircle(
        color = Color.Cyan,
        radius = pointRadius,
        center = nodePosition + node.entryPointOffset,
        style = Stroke(width = 2f)
    )
    // Точки выхода (справа)
    node.choicePointOffsets.forEach { offset ->
        drawCircle(
            color = Color.Magenta,
            radius = pointRadius,
            center = nodePosition + offset
        )
    }
}

fun DrawScope.drawEdge(startNode: GraphNode, endNode: GraphNode, startChoiceIndex: Int) {
    val startPoint = startNode.position + startNode.choicePointOffsets[startChoiceIndex]
    val endPoint = endNode.position + endNode.entryPointOffset

    // Рисуем линию (может быть кривой Безье)
    val path = Path()
    path.moveTo(startPoint.x, startPoint.y)
    // Добавляем контрольные точки для кривой
    val ctrlOffset = 50f * (if ((startPoint.x > endPoint.x)) -1f else 1f) // Смещение контрольных точек
    path.cubicTo(
        startPoint.x + ctrlOffset, startPoint.y,
        endPoint.x - ctrlOffset, endPoint.y,
        endPoint.x, endPoint.y
    )
    drawPath(
        path = path,
        color = Color.Blue,
        style = Stroke(width = 3f)
    )

    // Рисуем стрелку на конце линии
    drawArrowHead(endPoint, startPoint)
}

// Отрисовка временной линии при перетаскивании нового ребра
fun DrawScope.drawNewEdgeLine(startPoint: Offset, endPoint: Offset) {
    drawLine(
        color = Color.Red,
        start = startPoint,
        end = endPoint,
        strokeWidth = 4f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
    )
    drawCircle(Color.Red, radius = 8f, center = endPoint)
}

// Отрисовка наконечника стрелки
fun DrawScope.drawArrowHead(to: Offset, from: Offset) {
    val arrowLength = 20f
    val arrowAngle = 25f // Градусы

    val angleRad = kotlin.math.atan2(to.y - from.y, to.x - from.x) // Угол линии

    val angle1 = angleRad + Math.toRadians(arrowAngle.toDouble())
    val angle2 = angleRad - Math.toRadians(arrowAngle.toDouble())

    val x1 = to.x - arrowLength * kotlin.math.cos(angle1).toFloat()
    val y1 = to.y - arrowLength * kotlin.math.sin(angle1).toFloat()
    val x2 = to.x - arrowLength * kotlin.math.cos(angle2).toFloat()
    val y2 = to.y - arrowLength * kotlin.math.sin(angle2).toFloat()

    val arrowPath = Path().apply {
        moveTo(to.x, to.y)
        lineTo(x1, y1)
        moveTo(to.x, to.y)
        lineTo(x2, y2)
    }
    drawPath(arrowPath, Color.Blue, style = Stroke(width = 3f))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewGraphCanvas() {
    // Пример тестовых данных
    val nodes = remember {
        mutableStateMapOf(
            "1" to GraphNode(id = "1", previewText = "Начало истории", position = Offset(50f, 50f), isStartNode = true),
            "2" to GraphNode(id = "2", previewText = "Развилка сюжета", position = Offset(300f, 200f)),
            "3" to GraphNode(id = "3", previewText = "Концовка 1", position = Offset(550f, 50f), isEndNode = true),
            "4" to GraphNode(id = "4", previewText = "Концовка 2", position = Offset(550f, 400f), isEndNode = true)
        )
    }
    val edges = remember {
        mutableStateListOf(
            GraphEdge(id = "e1", startNodeId = "1", endNodeId = "2", startChoiceIndex = 0),
            GraphEdge(id = "e2", startNodeId = "2", endNodeId = "3", startChoiceIndex = 0),
            GraphEdge(id = "e3", startNodeId = "2", endNodeId = "4", startChoiceIndex = 1)
        )
    }

    val uiState = remember {
        mutableStateOf(StoryGraphUiState(
            nodes = nodes,
            edges = edges
        ))
    }

    val textMeasurer = rememberTextMeasurer() // Для измерения текста на Canvas

    val density = LocalDensity.current
    val hitSlopPx = with(density) { 20.dp.toPx() } // Пример

    // -- Переменные для обработки жестов --
    var dragNodeId: String? = null
    var dragEdgeStartInfo: Triple<String, Int, Offset>? = null // nodeId, choiceIndex, startOffset

    // Вспомогательные функции (заглушки для preview)
    val onPan: (Offset) -> Unit = {}
    val onZoom: (zoomChange: Float, centroid: Offset) -> Unit = {_, _ ->}
    val onNodeClick: (nodeId: String?) -> Unit = {}
    val onNodeDragStart: (nodeId: String) -> Unit = {}
    val onNodeDrag: (nodeId: String, dragAmount: Offset) -> Unit = { nodeId, dragAmount ->
        // Обновляем позицию узла
        nodes[nodeId]?.let { node ->
            nodes[nodeId] = node.copy(position = node.position + dragAmount)
            uiState.value = uiState.value.copy(nodes = nodes.toMap()) // Force recomposition
        }
    }
    val onNodeDragEnd: (nodeId: String) -> Unit = {}
    val onEdgeDragStart: (nodeId: String, choiceIndex: Int, startPosition: Offset) -> Unit = {_, _, _ ->}
    val onEdgeDrag: (currentPosition: Offset) -> Unit = {}
    val onEdgeDragEnd: (endNodeId: String?) -> Unit = {}

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Preview Graph Canvas") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            GraphCanvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(alpha = 0.8f)),
                uiState = uiState.value,
                textMeasurer = textMeasurer,
                onPan = onPan,
                onZoom = onZoom,
                onNodeClick = onNodeClick,
                onNodeDragStart = onNodeDragStart,
                onNodeDrag = onNodeDrag,
                onNodeDragEnd = onNodeDragEnd,
                onEdgeDragStart = onEdgeDragStart,
                onEdgeDrag = onEdgeDrag,
                onEdgeDragEnd = onEdgeDragEnd
            )
        }
    }
}

fun DrawScope.drawEdge(startPosition: Offset, endPosition: Offset) {
    val strokeWidth = 3f
    val color = Color.Blue

    // Рисуем линию (можно использовать кривую Безье для красоты)
    val path = Path()
    path.moveTo(startPosition.x, startPosition.y)

    // Рассчитываем контрольные точки для кривой Безье
    // Смещение зависит от расстояния и направления
    val dx = endPosition.x - startPosition.x
    val dy = endPosition.y - startPosition.y
    // Простое смещение по X для примера
    val ctrlOffsetHorizontal = (dx / 2f).coerceIn(-150f, 150f) // Ограничим смещение
    // Можно добавить и вертикальное смещение, чтобы избежать наложения на узлы
    val ctrlOffsetYStart = 0f
    val ctrlOffsetYEnd = 0f

    path.cubicTo(
        startPosition.x + ctrlOffsetHorizontal, startPosition.y + ctrlOffsetYStart, // Контрольная точка 1 (у начала)
        endPosition.x - ctrlOffsetHorizontal, endPosition.y + ctrlOffsetYEnd,     // Контрольная точка 2 (у конца)
        endPosition.x, endPosition.y                            // Конечная точка
    )

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )

    // Рисуем стрелку на конце линии
    drawArrowHead(endPosition, startPosition + Offset(ctrlOffsetHorizontal, ctrlOffsetYStart)) // Указываем на первую контрольную точку для направления
}

@Composable
fun MyScreen() {
    PreviewGraphCanvas()
}
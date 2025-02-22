package com.example.mobileappgamedevelopment

import android.os.Handler
import android.os.Looper
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

class GameScene : IScene {
    override val entities: MutableList<Entity> = mutableListOf()
    override lateinit var entityManager: EntityManager
    override var sceneManager: SceneManager? = null
    override val lines: MutableList<LineInfo> = mutableListOf()
    override lateinit var viewModel: MainViewModel

    lateinit var coinsText : TextInfo

    val gridWidth = 6
    val gridHeight = 6
    val gridMinX = -0.4f
    val gridMaxX = 0.4f
    val gridMinY = 0.31f
    val gridMaxY = -0.59f
    val cellWidth = (gridMaxX - gridMinX) / gridWidth
    val cellHeight = (gridMaxY - gridMinY) / gridHeight
    val gridThickness = 0.01f
    val gridColor = floatArrayOf(0.737f, 0.478f, 0.349f, 1f)

    val lightColor = floatArrayOf(0.95f, 0.85f, 0.60f, 1f) // Light beige
    val darkColor = floatArrayOf(0.80f, 0.60f, 0.40f, 1f) // Darker beige

    var ori_pos = floatArrayOf(0.0f, 0.0f, 0.0f)

    val cellColors = Array(gridWidth) { x ->
        Array(gridHeight) { y ->
            if ((x + y) % 2 == 0) lightColor else darkColor
        }
    }

    private var draggingEntity: Entity? = null
    private var isDragging = false
    private var isHolding = false
    private val holdHandler = Handler(Looper.getMainLooper())

    //send to cafe booleans
    private var sendToCafe_cake = false
    private var sendToCafe_cupcake = false
    private var sendToCafe_latte = false


    private val holdRunnable = Runnable {
        if (draggingEntity != null) {
            isHolding = true
            isDragging = true  // Enable dragging mode
        }
    }

    lateinit var toShopSceneButton: Entity
    lateinit var latte: Entity
    lateinit var cupcake: Entity
    lateinit var cake: Entity

    // Producers
    val producer_seed = R.drawable.seedpack
    val producer_wheatplant = R.drawable.wheatplant
    val producer_book = R.drawable.book

    // Mapping producers to their respective ingredients
    private val producerToIngredient = mapOf(
        producer_seed to R.drawable.seed,
        producer_wheatplant to R.drawable.wheat,
        producer_book to R.drawable.paper
    )
    private val mergeChains = mapOf(
        R.drawable.seed to listOf(R.drawable.seed,R.drawable.plant, R.drawable.strawberry, R.drawable.chocolate, R.drawable.vanilla),
        R.drawable.paper to listOf(R.drawable.paper,R.drawable.stackofpaper, R.drawable.plastic, R.drawable.wrapper, R.drawable.cup ),
        R.drawable.wheat to listOf(R.drawable.wheat,R.drawable.stackfowheat, R.drawable.flour, R.drawable.sponge, R.drawable.cupcake)
    )


    override fun onSurfaceCreated() {
        val lightColor = floatArrayOf(0.984f, 0.835f, 0.588f, 1f) // F4D596 (Pale Yellow)
        val darkColor = floatArrayOf(0.859f, 0.694f, 0.475f, 1f) // DB

        for (x in 0 until gridWidth) {
            for (y in 0 until gridHeight) {
                val color = if ((x + y) % 2 == 0) lightColor else darkColor

                // Calculate the center position of the square
                val centerX = gridMinX + x * cellWidth + cellWidth / 2
                val centerY = gridMinY + y * cellHeight + cellHeight / 2

                // Use thick lines to fill the cell
                val thicknessX = cellWidth * 0.95f // Almost full width
                val thicknessY = cellHeight * 0.95f // Almost full height

                // Create filled rectangles using horizontal and vertical thick lines
                lines.add(LineInfo(floatArrayOf(centerX - cellWidth / 2, centerY, 0f), floatArrayOf(centerX + cellWidth / 2, centerY, 0f), thicknessY, color))
                lines.add(LineInfo(floatArrayOf(centerX, centerY - cellHeight / 2, 0f), floatArrayOf(centerX, centerY + cellHeight / 2, 0f), thicknessX, color))
            }
        }

        // Draw grid lines on top
        for (i in 0..gridHeight) {
            val y = gridMinY + i * cellHeight
            val start = floatArrayOf(gridMinX, y, 0f)
            val end = floatArrayOf(gridMaxX, y, 0f)
            lines.add(LineInfo(start, end, gridThickness, gridColor))
        }

        for (i in 0..gridWidth) {
            val x = gridMinX + i * cellWidth
            val start = floatArrayOf(x, gridMinY, 0f)
            val end = floatArrayOf(x, gridMaxY, 0f)
            lines.add(LineInfo(start, end, gridThickness, gridColor))
        }

        // Add game entities
        addEntityToCell(2, 3, producer_book)
        addEntityToCell(5, 5, producer_seed)
        addEntityToCell(3, 5, producer_wheatplant)


        //testing========================================
        addEntityToCell(4, 5, R.drawable.strawberrcake)
        addEntityToCell(0, 1, R.drawable.cupcake)
        addEntityToCell(0, 2, R.drawable.latte)
        //==================================================

        toShopSceneButton = entityManager.createEntity(R.drawable.shopicon)
        toShopSceneButton.position = floatArrayOf(0.3f, -0.87f, 0f)
        toShopSceneButton.scale = floatArrayOf(0.21f, 0.21f, 0.21f)
        entities.add(toShopSceneButton)

        //button
        cake = entityManager.createEntity(R.drawable.nrycake)
        cake.position = floatArrayOf(-0.30f, 0.48f, 0f)
        cake.scale = floatArrayOf(0.19f, 0.26f, 0.21f)
        entities.add(cake)

        cupcake = entityManager.createEntity(R.drawable.nrycupcake)
        cupcake.position = floatArrayOf(-0.10f, 0.48f, 0f)
        cupcake.scale = floatArrayOf(0.19f, 0.26f, 0.21f)
        entities.add(cupcake)

        latte = entityManager.createEntity(R.drawable.nrylatte)
        latte.position = floatArrayOf(0.10f, 0.48f, 0f)
        latte.scale = floatArrayOf(0.19f, 0.26f, 0.21f)
        entities.add(latte)


        coinsText = TextInfo("100")
        coinsText.offsetX = 150.dp
        coinsText.offsetY = (-230).dp

        viewModel.addTextInfo(coinsText)
    }


    override fun onSurfaceChanged() {}

    override fun update() {
        entityManager.setBackgroundTexture(R.drawable.gamescreenbg)

        //strawberrycake
        if(doesEntityExist(R.drawable.strawberrcake)){
            cake.textureId = R.drawable.scsendtocafe
            sendToCafe_cake= true
        }
        //cupcake
        if(doesEntityExist(R.drawable.cupcake)){
            cupcake.textureId = R.drawable.cupcakesendtocafe
            sendToCafe_cupcake = true
        }
        //latte
        if(doesEntityExist(R.drawable.latte)){
            latte.textureId = R.drawable.lattesendtocafe
            sendToCafe_latte = true
        }

        //set back to false
        if(!sendToCafe_cake){
            cake.textureId = R.drawable.nrycake
            cake.position = floatArrayOf(-0.30f, 0.48f, 0f)

        }
        if(!sendToCafe_cupcake){
            cupcake.textureId = R.drawable.nrycupcake
            cupcake.position = floatArrayOf(-0.10f, 0.48f, 0f)

        }
        if(!sendToCafe_latte){
            latte.textureId = R.drawable.nrylatte
            latte.scale = floatArrayOf(0.19f, 0.26f, 0.21f)

        }
    }

    fun getEntityDrawableInCell(xIndex: Int, yIndex: Int): Int? {
        for (entity in entities) {
            val entityX = ((entity.position[0] - gridMinX) / cellWidth).toInt()
            val entityY = ((entity.position[1] - gridMinY) / cellHeight).toInt()

            if (entityX == xIndex && entityY == yIndex) {
                return entity.textureId // Return the drawable ID of the entity
            }
        }
        return null // Return null if no entity is found in the cell
    }

    override fun onActionDown(normalizedX: Float, normalizedY: Float) {

        if (toShopSceneButton.contains(normalizedX, normalizedY)) {

            sceneManager?.setScene(ShopScene::class, viewModel)
        } else if(sendToCafe_cake && cake.contains(normalizedX, normalizedY)){
            val deleteEntity: Entity = getEntityByTextureId(R.drawable.strawberrcake) ?: return
            deleteEntity(deleteEntity)
            viewModel.addFoodItem("cake")
            sendToCafe_cake = false
        }
        else if(sendToCafe_cupcake && cupcake.contains(normalizedX, normalizedY)){
            val deleteEntity: Entity = getEntityByTextureId(R.drawable.cupcake) ?: return
            deleteEntity(deleteEntity)
            viewModel.addFoodItem("cupcake")
            sendToCafe_cupcake = false
        }
        else if(sendToCafe_latte && latte.contains(normalizedX, normalizedY)){
            val deleteEntity: Entity = getEntityByTextureId(R.drawable.latte) ?: return
            deleteEntity(deleteEntity)
            viewModel.addFoodItem("latte")
            sendToCafe_latte = false
        }
        else {
            synchronized(entities) {


                for (entity in entities.reversed()) {
                    if (entity.contains(normalizedX, normalizedY)) {
                        draggingEntity = entity
                        isDragging = false
                        isHolding = false
                        ori_pos = entity.position.copyOf()

                        viewModel.audioManager.playAudio(R.raw.uiclick)
                        holdHandler.postDelayed(holdRunnable, 85)
                        return
                    }
                }

                viewModel.updateUserCoins(150)

                viewModel.removeTextInfo(coinsText)
                coinsText.text = "150"
                viewModel.addTextInfo(coinsText)
            }

        }
    }

        fun getEntityInCell(xIndex: Int, yIndex: Int, excludeEntity: Entity? = null): Entity? {
            return entities.find { entity ->
                if (entity == excludeEntity) return@find false  // Exclude the currently held entity
                val entityX = ((entity.position[0] - gridMinX) / cellWidth).toInt()
                val entityY = ((entity.position[1] - gridMinY) / cellHeight).toInt()
                entityX == xIndex && entityY == yIndex
            }
        }

        fun isCellOccupied(xIndex: Int, yIndex: Int, excludeEntity: Entity? = null): Boolean {
            val occupiedCells = entities.filter { it != excludeEntity } // Exclude dragging entity
                .map { entity ->
                    val entityX = ((entity.position[0] - gridMinX) / cellWidth).toInt()
                    val entityY = ((entity.position[1] - gridMinY) / cellHeight).toInt()
                    entityX to entityY
                }.toSet()

            println("Occupied cells (excluding current entity): $occupiedCells") // DEBUG PRINT

            return occupiedCells.contains(xIndex to yIndex)

        }

        override fun onActionMove(normalizedDx: Float, normalizedDy: Float) {
            if (isDragging && draggingEntity != null) {
                draggingEntity!!.position[0] += normalizedDx
                draggingEntity!!.position[1] += normalizedDy
            }
        }

        fun findNearestEmptyCell(
            startX: Int,
            startY: Int,
            excludeEntity: Entity? = null
        ): Pair<Int, Int> {
            val searchOffsets = listOf(
                Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0), // Adjacent cells
                Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1) // Diagonal cells
            )

            for ((dx, dy) in searchOffsets) {
                val newX = startX + dx
                val newY = startY + dy

                if (newX in 0 until gridWidth && newY in 0 until gridHeight && !isCellOccupied(
                        newX,
                        newY,
                        excludeEntity
                    )
                ) {
                    return newX to newY // ✅ Found an empty spot
                }
            }

            return -1 to -1 // 🚨 No empty space found
        }


        override fun onActionUp() {

            holdHandler.removeCallbacks(holdRunnable) // Stop hold detection

            if (draggingEntity != null) {
                if (!isHolding) {
                    // ✅ If the user didn't hold, play a tap sound
                    viewModel.audioManager.playAudio(R.raw.scoop)
                }
                // Convert entity position to grid indices BEFORE updating position
                val previousX =
                    ((ori_pos[0] - gridMinX) / cellWidth).toInt().coerceIn(0, gridWidth - 1)
                val previousY =
                    ((ori_pos[1] - gridMinY) / cellHeight).toInt().coerceIn(0, gridHeight - 1)

                val gridX = ((draggingEntity!!.position[0] - gridMinX) / cellWidth).toInt()
                    .coerceIn(0, gridWidth - 1)
                val gridY = ((draggingEntity!!.position[1] - gridMinY) / cellHeight).toInt()
                    .coerceIn(0, gridHeight - 1)

                println(
                    "Trying to place entity at ($gridX, $gridY) - Occupied before update: ${
                        isCellOccupied(
                            gridX,
                            gridY,
                            excludeEntity = draggingEntity
                        )
                    }"
                )

                //spawning
                if (!isHolding) {
                    val ingredientTexture = producerToIngredient[draggingEntity!!.textureId]
                    if (ingredientTexture != null) {
                        val (xIndex, yIndex) = findNextAvailableGridCellNearProducer(draggingEntity!!)
                        if (xIndex != -1 && yIndex != -1) {
                            addEntityToCell(xIndex, yIndex, ingredientTexture) // Spawn ingredient
                            println("Ingredient spawned at ($xIndex, $yIndex)")
                        }
                    }
                } else {

                    if (!isCellOccupied(gridX, gridY, excludeEntity = draggingEntity)) {
                        viewModel.audioManager.playAudio(R.raw.mainmenuclick)
                        draggingEntity!!.position =
                            getCellCenter(gridX, gridY, gridMinX, gridMinY, cellWidth, cellHeight)
                        println("Entity placed successfully at ($gridX, $gridY)")

                    } else {
                        val existingEntity =
                            getEntityInCell(gridX, gridY, excludeEntity = draggingEntity)
                        val newTexture = getNextMergeTexture(draggingEntity!!.textureId)
                        if (existingEntity!!.textureId == draggingEntity!!.textureId) {
                            println("same entity")
                            newTexture?.let {
                                // ✅ Only update if `newTexture` is NOT null
                                existingEntity.textureId = it
                                viewModel.audioManager.playAudio(R.raw.shaking)
                                println("Merged! Entity at ($gridX, $gridY) transformed into new texture.")

                                // ✅ Delete the second entity off-screen
                                deleteEntity(draggingEntity!!)
                                println("Dragged entity deleted after merging.")
                            } ?: run {
                                println("No further upgrades available.")

                                // Search for the nearest available cell, excluding the dragging entity itself
                                val (newX, newY) = findNearestEmptyCell(
                                    gridX,
                                    gridY,
                                    excludeEntity = draggingEntity
                                )

                                if (newX != -1 && newY != -1) {
                                    draggingEntity!!.position =
                                        getCellCenter(
                                            newX,
                                            newY,
                                            gridMinX,
                                            gridMinY,
                                            cellWidth,
                                            cellHeight
                                        )

                                    println("Cell occupied! Moved entity to nearest empty cell ($newX, $newY)")
                                } else {
                                    draggingEntity!!.position = ori_pos.copyOf()
                                    println("No empty cells nearby! Returning entity to original position ($previousX, $previousY).")
                                }
                            }

                        } else {

                            //some special merges
                            val existingEntity =
                                getEntityInCell(gridX, gridY, excludeEntity = draggingEntity)
                            val pair = setOf(existingEntity!!.textureId, draggingEntity!!.textureId)

                            if (pair == setOf(R.drawable.strawberry, R.drawable.sponge)) {
                                existingEntity.textureId = R.drawable.strawberrcake

                                println("Merged! Entity at ($gridX, $gridY) transformed into new texture.")

                                // ✅ Delete the second entity off-screen
                                deleteEntity(draggingEntity!!)
                                println("Dragged entity deleted after merging.")
                            } else if (pair == setOf(R.drawable.wrapper, R.drawable.cupcake)) {
                                existingEntity.textureId = R.drawable.cupcake

                                println("Merged! Entity at ($gridX, $gridY) transformed into new texture.")

                                // ✅ Delete the second entity off-screen
                                deleteEntity(draggingEntity!!)
                                println("Dragged entity deleted after merging.")
                            } else if (pair == setOf(R.drawable.vanilla, R.drawable.cup)) {
                                //change in the future
                                existingEntity.textureId = R.drawable.latte

                                println("Merged! Entity at ($gridX, $gridY) transformed into new texture.")

                                // ✅ Delete the second entity off-screen
                                deleteEntity(draggingEntity!!)
                                println("Dragged entity deleted after merging.")
                            } else {
                                // Search for the nearest available cell, excluding the dragging entity itself
                                val (newX, newY) = findNearestEmptyCell(
                                    gridX,
                                    gridY,
                                    excludeEntity = draggingEntity
                                )

                                if (newX != -1 && newY != -1) {
                                    draggingEntity!!.position =
                                        getCellCenter(
                                            newX,
                                            newY,
                                            gridMinX,
                                            gridMinY,
                                            cellWidth,
                                            cellHeight
                                        )
                                    viewModel.audioManager.playAudio(R.raw.scoop2)
                                    println("Cell occupied! Moved entity to nearest empty cell ($newX, $newY)")
                                } else {
                                    draggingEntity!!.position = ori_pos.copyOf()
                                    println("No empty cells nearby! Returning entity to original position ($previousX, $previousY).")
                                }
                            }
                        }
                    }
                }
            }

            // Reset states
            draggingEntity = null
            isDragging = false
            isHolding = false
        }


    fun getMergeKey(currentTexture: Int): Int? {
        return mergeChains.entries.find { it.value.contains(currentTexture) }?.key
    }

    fun getNextMergeTexture(currentTexture: Int): Int? {

        val textureKey = if (currentTexture > 5) getMergeKey(currentTexture) else currentTexture

        if (textureKey == null) {
            println("🚨 ERROR: Could not find key for texture ID $currentTexture")
            return null
        }

        val mergeList = mergeChains[textureKey] ?: return null

        println("🔍 Checking merge list: $mergeList for texture ID $currentTexture (Key: $textureKey)")

        val currentIndex = mergeList.indexOf(currentTexture)

        if (currentIndex == -1) {
            println("🚨 ERROR: Texture ID $currentTexture not found in merge list: $mergeList")
            return null
        }

        if (currentIndex < mergeList.size - 1) {
            val nextTexture = mergeList[currentIndex + 1]
            println("✅ Next upgrade found! $currentTexture → $nextTexture")
            return nextTexture
        }

        println("🔴 No further upgrades available for texture ID $currentTexture")
        return null
    }

    fun getCellCenter(xIndex: Int, yIndex: Int, gridMinX: Float, gridMinY: Float, cellWidth: Float, cellHeight: Float): FloatArray {
        val centerX = gridMinX + (xIndex + 0.5f) * cellWidth
        val centerY = gridMinY + (yIndex + 0.5f) * cellHeight
        return floatArrayOf(centerX, centerY, 0f) // Z-coordinate is 0
    }

    fun addEntityToCell(xIndex: Int, yIndex: Int, textureId: Int) {
        if (!isCellOccupied(xIndex, yIndex)) {
            val cellCenter = getCellCenter(xIndex, yIndex, gridMinX, gridMinY, cellWidth, cellHeight)
            val entity = entityManager.createEntity(textureId)
            entity.position = cellCenter
            entity.scale = floatArrayOf(0.07f, 0.07f, 1f)
            entities.add(entity)

            println("✅ Entity Created at ($xIndex, $yIndex) with Texture ID: $textureId")
        }
    }

    fun doesEntityExist(drawableId: Int): Boolean {
        return entities.any { it.textureId == drawableId }
    }

    fun getEntityByTextureId(textureId: Int): Entity? {
        return entities.find { it.textureId == textureId }
    }


    fun findNextAvailableGridCellNearProducer(producer: Entity): Pair<Int, Int> {
        val producerX = ((producer.position[0] - gridMinX) / cellWidth).toInt()
        val producerY = ((producer.position[1] - gridMinY) / cellHeight).toInt()

        val occupiedCells = entities.map { entity ->
            val xIndex = ((entity.position[0] - gridMinX) / cellWidth).toInt()
            val yIndex = ((entity.position[1] - gridMinY) / cellHeight).toInt()
            xIndex to yIndex
        }.toSet()

        val searchOrder = listOf(
            Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1), // Adjacent cells
            Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1) // Diagonal cells
        )

        for ((dx, dy) in searchOrder) {
            val newX = producerX + dx
            val newY = producerY + dy
            if (newX in 0 until gridWidth && newY in 0 until gridHeight && !occupiedCells.contains(newX to newY)) {
                return newX to newY
            }
        }
        return -1 to -1
    }
}



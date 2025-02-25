package com.example.mobileappgamedevelopment
/**
 * @file ShopScene.kt
 * @brief Defines the ShopScene class, which manages the shop environment, customers, and food items.
 */

import android.os.Handler
import android.os.Looper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * @class ShopScene
 * @brief Represents the shop scene where players manage customers and food sales.
 */
class ShopScene : IScene {
    override val entities: MutableList<Entity> = mutableListOf()
    override lateinit var entityManager: EntityManager
    override var sceneManager: SceneManager? = null
    override val lines: MutableList<LineInfo> = mutableListOf()
    override lateinit var viewModel: MainViewModel

    private val customerQueue: ArrayDeque<Entity> = ArrayDeque()
    private val maxCustomers = 4
    private var spawnIndex = 0


    /**
     * Path followed by customers entering the shop.
     */
    private val path = listOf(
        floatArrayOf(0.3f, 0.45f, 0f),
        floatArrayOf(0.3f, 0.25f, 0f),
        floatArrayOf(-0.15f, 0.25f, 0f),
    )


    /**
     * Paths from the counter to tables for customer seating.
     */
    private val pathToTables = listOf(
        listOf(
            floatArrayOf(-0.15f, 0.25f, 0f),
            floatArrayOf(-0.15f, 0.1f, 0f), // Table 1
        ),
        listOf(
            floatArrayOf(-0.15f, 0.25f, 0f),
            floatArrayOf(0.15f, 0.1f, 0f), // Table 2
        ),
        listOf(
            floatArrayOf(-0.15f, 0.25f, 0f),
            floatArrayOf(-0.35f, 0.25f, 0f),
            floatArrayOf(-0.35f, -0.3f, 0f),
            floatArrayOf(-0.15f, -0.3f, 0f), // Table 3
        ),
        listOf(
            floatArrayOf(-0.15f, 0.25f, 0f),
            floatArrayOf(0f, 0.25f, 0f),
            floatArrayOf(0f, -0.3f, 0f),
            floatArrayOf(0.15f, -0.3f, 0f), // Table 4
        )
    )

    // UI Elements and food items
    lateinit var toGameSceneButton: Entity
    lateinit var table: Entity
    lateinit var plate: Entity

    //food items
    lateinit var cake: Entity
    lateinit var cupcake: Entity
    lateinit var latte: Entity

    lateinit var textBG: Entity

    lateinit var foodItemText: TextInfo

    lateinit var cakeCount: TextInfo
    lateinit var cupcakeCount: TextInfo
    lateinit var latteCount: TextInfo

    //coins
    lateinit var coinsText : TextInfo
    lateinit var coinIcon: Entity


    /**
     * @brief Initializes the scene, including UI elements and food items.
     */
    override fun onSurfaceCreated() {

        startCustomerSpawner()

        //plate
        plate  =entityManager.createEntity(R.drawable.plate)
        plate.position = mutableListOf(-0.33f, 0.41f, 0f)
        plate.scale = mutableListOf(0.15f, 0.1f, 0.1f)
        entities.add(plate)

        plate  =entityManager.createEntity(R.drawable.plate)
        plate.position = mutableListOf(-0.15f, 0.41f, 0f)
        plate.scale = mutableListOf(0.15f, 0.1f, 0.1f)
        entities.add(plate)

        plate  =entityManager.createEntity(R.drawable.plate)
        plate.position = mutableListOf(0.03f, 0.41f, 0f)
        plate.scale = mutableListOf(0.15f, 0.1f, 0.1f)
        entities.add(plate)

//        repeat(1) { index ->
//            val entity = entityManager.createEntity(R.drawable.catfront)
//            entity.position = floatArrayOf(-0.8f, 0.8f, 0f)
//            entity.scale = floatArrayOf(0.2f, 0.2f, 1f)
//            entity.userData["path"] = path
//            entity.userData["progress"] = 0f
//            entity.userData["speed"] = 0.5f
//            entities.add(entity)
//        }

        toGameSceneButton = entityManager.createEntity(R.drawable.bakery)
        toGameSceneButton.position = mutableListOf(0.3f, -0.87f, 0f)
        toGameSceneButton.scale = mutableListOf(0.22f, 0.22f, 0.25f)
        entities.add(toGameSceneButton)

        //tables
        table  =entityManager.createEntity(R.drawable.table)
        table.position = mutableListOf(-0.16f, -0.1f, 0f)
        table.scale = mutableListOf(0.22f, 0.22f, 0.25f)
        table.layerId = 1
        entities.add(table)
        table  =entityManager.createEntity(R.drawable.table)
        table.position = mutableListOf(-0.16f, -0.5f, 0f)
        table.scale = mutableListOf(0.22f, 0.22f, 0.25f)
        table.layerId = 1
        entities.add(table)
        table  =entityManager.createEntity(R.drawable.table)
        table.position = mutableListOf(0.16f, -0.1f, 0f)
        table.scale = mutableListOf(0.22f, 0.22f, 0.25f)
        table.layerId = 1
        entities.add(table)
        table  =entityManager.createEntity(R.drawable.table)
        table.position = mutableListOf(0.16f, -0.5f, 0f)
        table.scale = mutableListOf(0.22f, 0.22f, 0.25f)
        table.layerId = 1
        entities.add(table)


        //add food items
        cake =  entityManager.createEntity(R.drawable.strawberrcake)
        //cake.position = mutableListOf(-0.325f, 0.45f, 0f)
        cake.position = mutableListOf(-1f, -1f, 0f)
        cake.scale = mutableListOf(0.14f, 0.14f, 0.14f)
        entities.add(cake)

        cupcake =  entityManager.createEntity(R.drawable.cupcake)
        //cupcake.position = floatArrayOf(-0.155f, 0.435f, 0f)
        cupcake.position = mutableListOf(-1f, -1f, 0f)
        cupcake.scale = mutableListOf(0.14f, 0.14f, 0.14f)
        entities.add(cupcake)

        latte =  entityManager.createEntity(R.drawable.latte)
        //latte.position = floatArrayOf(0.03f, 0.445f, 0f)
        latte.position = mutableListOf(-1f, -1f, 0f)
        latte.scale = mutableListOf(0.14f, 0.14f, 0.14f)
        entities.add(latte)

        // Initialize TextBG
        textBG = entityManager.createEntity(R.drawable.app_bg)
        textBG.scale = mutableListOf(0.2f, 0.12f, 0.2f)
        textBG.position = mutableListOf(-1f, 1f, 0f)
        entities.add(textBG)

        foodItemText = TextInfo("hello")
        foodItemText.fontSize = 20.sp
        foodItemText.offsetX = -500.dp
        foodItemText.offsetY = (-500).dp
        viewModel.addTextInfo(foodItemText)

        // Initialize food item count
        cakeCount = TextInfo("0")
        cakeCount.fontSize = 20.sp
        cakeCount.offsetX = -500.dp
        cakeCount.offsetY = (-500).dp
        viewModel.addTextInfo(cakeCount)

        cupcakeCount = TextInfo("0")
        cupcakeCount.fontSize = 20.sp
        cupcakeCount.offsetX = -500.dp
        cupcakeCount.offsetY = (-500).dp
        viewModel.addTextInfo(cupcakeCount)

        latteCount = TextInfo("0")
        latteCount.fontSize = 20.sp
        latteCount.offsetX = -500.dp
        latteCount.offsetY = (-500).dp
        viewModel.addTextInfo(latteCount)

        // Initialize coins text
        coinsText = TextInfo("0") // Start with 0, will be updated
        coinsText.offsetX = 160.dp
        coinsText.offsetY = (-393).dp
        viewModel.addTextInfo(coinsText)

        coinIcon = entityManager.createEntity(R.drawable.coin)
        coinIcon.position = mutableListOf(0.23f, 0.85f, 0f)
        coinIcon.scale = mutableListOf(0.07f, 0.07f, 0.07f)
        entities.add(coinIcon)

        // 🪙 Observe changes in coins LiveData and update UI
        Handler(Looper.getMainLooper()).post {
            viewModel.coins.observeForever { newCoins ->
                viewModel.removeTextInfo(coinsText)
                coinsText.text = "$newCoins" // Update text dynamically
                println("🪙 Coins updated in ShopScene: $newCoins") // Debug log
                viewModel.addTextInfo(coinsText)
            }
        }
    }

    /**
     * @brief Handles entering the scene, including resetting UI states.
     */
    override fun onEnter() {
        Handler(Looper.getMainLooper()).post {
            viewModel.removeTextInfo(coinsText)
            viewModel.addTextInfo(coinsText)
        }

        viewModel.audioManager.playBGM(R.raw.endofdayloop)

    }

    override fun onSurfaceChanged() {

    }

    /**
     * @brief Spawns customers at intervals to simulate shop traffic.
     */
    private fun startCustomerSpawner() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                spawnCustomer()
                delay(3000) // Spawn every 5 seconds
            }
        }
    }

    /**
     * @brief Spawns a customer, assigns them a food order, and moves them along the path.
     */
    private fun spawnCustomer() {
        val availableFood = mutableListOf<String>()

        if (viewModel.getFoodItemCount("cake") > 0) availableFood.add("cake")
        if (viewModel.getFoodItemCount("cupcake") > 0) availableFood.add("cupcake")
        if (viewModel.getFoodItemCount("latte") > 0) availableFood.add("latte")

        if (availableFood.isNotEmpty()) {
            val selectedFood = availableFood[Random.nextInt(availableFood.size)]

            // Create customer
            val customer = entityManager.createEntity(R.drawable.catfront)
            customer.position = path[0].toMutableList() // Start at entry point
            customer.scale = mutableListOf(0.2f, 0.2f, 1f)
            entities.add(customer)

            customer.userData["selectedFood"] = selectedFood
            val foodItem = customer.userData["selectedFood"] as String

            customerQueue.addLast(customer)

            // Assign a path to the customer (to the table)
            val tableIndex = spawnIndex % maxCustomers
            spawnIndex++

            val assignedPath = pathToTables[tableIndex]

            // Check if the customer can collect food
            CoroutineScope(Dispatchers.Main).launch {
                // Move to counter (food area)
                moveEntityAlongPath(customer, path, null)

                // Check if customer is at food area (path[2])
                val offset = 0.05f
                if (Math.abs(customer.position[0] - path[2][0]) < offset && Math.abs(customer.position[1] - path[2][1]) < offset) {
                    if (viewModel.getFoodItemCount(foodItem) != 0) {
                        // Decrease the stock of the selected food item
                        viewModel.decreaseFoodCount(foodItem)

                        // Remove food item from list if 0
                        if (viewModel.getFoodItemCount(foodItem) == 0) {
                            viewModel.removeFoodItem(foodItem)
                        }

                        // Add money for food purchase
                        viewModel.addCoins(5) // Add money for the customer buying food
                        coinsText.text = "${viewModel.coins.value ?: 0u}" // Update coin display

                        // Move to table after collecting food
                        moveEntityAlongPath(customer, assignedPath, null)

                        // Customer stays at the table for a while (3 seconds)
                        delay(3000)
                    }
                }

                // Move to exit
                val leavingPath = getExitPath(customer)
                CoroutineScope(Dispatchers.Main).launch {
                    moveEntityAlongPath(customer, leavingPath) {
                        entities.remove(customer) // Remove customer from scene after they leave
                    }
                }
            }
        } else {
            // Pause customer spawning if no food is available
            println("No food available, pausing customer spawning.")
        }
    }

    private fun getExitPath(customer: Entity): List<FloatArray> {
        val currentPos = customer.position
        val exitX = 0.35f // Move to this x-position before exiting

        return listOf(
            floatArrayOf(currentPos[0], currentPos[1], 0f),
            floatArrayOf(exitX, currentPos[1], 0f), // Move horizontally
            path[0] // Go straight to exit
        )
    }

    /**
     * @brief Moves an entity along a predefined path.
     * @param entity The entity to move.
     * @param path The path the entity follows.
     * @param onComplete Optional completion callback.
     */
    private suspend fun moveEntityAlongPath(entity: Entity, path: List<FloatArray>, onComplete: (() -> Unit)?) {
        if (path.isEmpty()) return // No movement if path is empty

        for (i in 0 until path.size - 1) {
            val start = path[i]
            val end = path[i + 1]
            val duration = 1.5f
            var t = 0f

            while (t < 1f) {
                delay(16) // Approx. 60 FPS
                t += 0.0167f * (1 / duration)
                entity.position = interpolate(start, end, t).toMutableList()
            }

            entity.position = end.toMutableList()
        }

        onComplete?.invoke()
    }

    /**
     * @brief Interpolates between two positions.
     * @param start The starting position.
     * @param end The ending position.
     * @param t The interpolation factor.
     * @return The interpolated position.
     */
    private fun interpolate(start: FloatArray, end: FloatArray, t: Float): FloatArray {
        return floatArrayOf(
            start[0] + (end[0] - start[0]) * t,
            start[1] + (end[1] - start[1]) * t,
            start[2] + (end[2] - start[2]) * t
        )
    }

    /**
     * @brief Updates the scene, including customer and UI updates.
     */
    override fun update() {
        coinsText.text = "${viewModel.coins.value ?: 0u}"

        if(viewModel.isFoodItemExists("cake")){
            cake.position = mutableListOf(-0.325f, 0.45f, 0f)

            // Update cakeCount
            Handler(Looper.getMainLooper()).post {
                val newCakeCount = viewModel.getFoodItemCount("cake")
                viewModel.removeTextInfo(cakeCount)
                cakeCount.text = "x $newCakeCount" // Update text dynamically
                println("Cake count updated in ShopScene: $newCakeCount") // Debug log
                viewModel.addTextInfo(cakeCount)
            }
        }
        else{
            cake.position = mutableListOf(-10f, -10f, 0f)
        }

        if(viewModel.isFoodItemExists("cupcake")){
            cupcake.position = mutableListOf(-0.155f, 0.435f, 0f)

            // Update cupcakeCount
            Handler(Looper.getMainLooper()).post {
                val newCupcakeCount = viewModel.getFoodItemCount("cupcake")
                viewModel.removeTextInfo(cupcakeCount)
                cupcakeCount.text = "x $newCupcakeCount" // Update text dynamically
                println("Cake count updated in ShopScene: $newCupcakeCount") // Debug log
                viewModel.addTextInfo(cupcakeCount)
            }
        }
        else if (viewModel.getFoodItemCount("cupcake") == 0){
            cupcake.position = mutableListOf(-10f, -10f, 0f)
        }

        if(viewModel.isFoodItemExists("latte")){
            latte.position = mutableListOf(0.03f, 0.445f, 0f)

            // Update latteCount
            Handler(Looper.getMainLooper()).post {
                val newLatteCount = viewModel.getFoodItemCount("latte")
                viewModel.removeTextInfo(latteCount)
                latteCount.text = "x $newLatteCount" // Update text dynamically
                println("Cake count updated in ShopScene: $newLatteCount") // Debug log
                viewModel.addTextInfo(latteCount)
            }
        }
        else if (viewModel.getFoodItemCount("latte") == 0){
            latte.position = mutableListOf(-10f, -10f, 0f)
        }

        synchronized(entities) {
            for (entity in entities) {
                if (entity == toGameSceneButton) continue

                // Safely retrieve user data fields
                val path = entity.userData["path"] as? List<FloatArray> ?: emptyList()
                var progress = entity.userData["progress"] as? Float ?: 0.1f
                val speed = entity.userData["speed"] as? Float ?: 0.1f

                // Skip the entity if the path is empty
                if (path.isEmpty()) {
                    continue
                }

                // Update progress
                progress += 0.0167f * speed
                while (progress >= path.size) {
                    progress -= path.size.toFloat()
                }

                // Interpolate position
                val segmentIndex = progress.toInt()
                val t = progress - segmentIndex
                val start = path[segmentIndex % path.size]
                val end = path[(segmentIndex + 1) % path.size]
                entity.position = interpolate(start, end, t).toMutableList()

                // Update progress in user data
                entity.userData["progress"] = progress
            }

        }

        entityManager.setBackgroundTexture(R.drawable.shopbg)
    }

    /**
     * @brief Handles touch interactions with the scene.
     * @param normalizedX X-coordinate of the touch event.
     * @param normalizedY Y-coordinate of the touch event.
     */
    override fun onActionDown(normalizedX: Float, normalizedY: Float) {
        viewModel.audioManager.playAudio(R.raw.uiclick)
        synchronized(entities) {
            if(toGameSceneButton.contains(normalizedX, normalizedY)){
                viewModel.removeTextInfo(coinsText)
                sceneManager?.setScene(GameScene::class, viewModel)
            }

            // Click on cake to display quantity
            if (cake.contains(normalizedX, normalizedY)) {
                textBG.scale = mutableListOf(0.2f, 0.12f, 0.2f)
                textBG.position = mutableListOf(-0.33f, 0.56f, 0f)

                foodItemText.text = "CAKE"
                foodItemText.offsetX = -155.dp
                foodItemText.offsetY = (-270).dp

                cakeCount.offsetX = -155.dp
                cakeCount.offsetY = (-250).dp
            }
            else if (cupcake.contains(normalizedX, normalizedY)) {
                textBG.scale = mutableListOf(0.27f, 0.12f, 0.2f)
                textBG.position = mutableListOf(-0.16f, 0.56f, 0f)

                foodItemText.text = "CUPCAKE"
                foodItemText.offsetX = -75.dp
                foodItemText.offsetY = (-270).dp

                cupcakeCount.offsetX = -75.dp
                cupcakeCount.offsetY = (-250).dp
            }
            else if (latte.contains(normalizedX, normalizedY)) {
                textBG.scale = mutableListOf(0.2f, 0.12f, 0.2f)
                textBG.position = mutableListOf(0.01f, 0.56f, 0f)

                foodItemText.text = "LATTE"
                foodItemText.offsetX = 5.dp
                foodItemText.offsetY = (-270).dp

                latteCount.offsetX = 5.dp
                latteCount.offsetY = (-250).dp
            }

            // Set a timer to hide text after 2 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                textBG.position = mutableListOf(-1f, 1f, 0f)

                foodItemText.offsetX = -500.dp
                foodItemText.offsetY = (-500).dp

                cakeCount.offsetX = -500.dp
                cakeCount.offsetY = (-500).dp

                cupcakeCount.offsetX = -500.dp
                cupcakeCount.offsetY = (-500).dp

                latteCount.offsetX = -500.dp
                latteCount.offsetY = (-500).dp
            }, 2000)
        }
    }

    override fun onActionMove(normalizedDx: Float, normalizedDy: Float) {
        entityManager.moveSelectedEntity(normalizedDx, normalizedDy)
    }

    override fun onActionUp() {
        entityManager.setSelectedEntity(null)
    }
}

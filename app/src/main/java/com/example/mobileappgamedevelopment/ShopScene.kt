package com.example.mobileappgamedevelopment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil3.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShopScene : IScene {
    override val entities: MutableList<Entity> = mutableListOf()
    override lateinit var entityManager: EntityManager
    override var sceneManager: SceneManager? = null
    override val lines: MutableList<LineInfo> = mutableListOf()
    override lateinit var viewModel: MainViewModel

    private val customerQueue: ArrayDeque<Entity> = ArrayDeque()
    private val customerTableMap = mutableMapOf<Entity, FloatArray>()
    private val maxCustomers = 4
    private var spawnIndex = 0

    private val path = listOf(
        floatArrayOf(0.3f, 0.45f, 0f), // door
        floatArrayOf(0.3f, 0.25f, 0f), // in front of door

    )

    private val tablePos  = listOf (
        floatArrayOf(-0.35f, -0.1f, 0f), // table 1
        floatArrayOf(0.35f, -0.1f, 0f), // table 2
        floatArrayOf(-0.35f, -0.5f, 0f), // table 3
        floatArrayOf(0.35f, -0.5f, 0f), // table 4
    )


    lateinit var toGameSceneButton: Entity
    lateinit var table: Entity
    lateinit var plate: Entity
    //food items
    lateinit var cake: Entity
    lateinit var cupcake: Entity
    lateinit var latte: Entity

    override fun onSurfaceCreated() {

        startCustomerSpawner()

        //plate
        plate  =entityManager.createEntity(R.drawable.plate)
        plate.position = floatArrayOf(-0.33f, 0.41f, 0f)
        plate.scale = floatArrayOf(0.15f, 0.1f, 0.1f)
        entities.add(plate)

        plate  =entityManager.createEntity(R.drawable.plate)
        plate.position = floatArrayOf(-0.15f, 0.41f, 0f)
        plate.scale = floatArrayOf(0.15f, 0.1f, 0.1f)
        entities.add(plate)

        plate  =entityManager.createEntity(R.drawable.plate)
        plate.position = floatArrayOf(0.03f, 0.41f, 0f)
        plate.scale = floatArrayOf(0.15f, 0.1f, 0.1f)
        entities.add(plate)

        /*repeat(1) { index ->
            val entity = entityManager.createEntity(R.drawable.placeholder_customer)
            entity.position = floatArrayOf(-0.8f, 0.8f, 0f)
            entity.scale = floatArrayOf(0.2f, 0.2f, 1f)
            entity.userData["path"] = path
            entity.userData["progress"] = 0f
            entity.userData["speed"] = 0.5f
            entities.add(entity)
        }*/

        toGameSceneButton = entityManager.createEntity(R.drawable.bakery)
        toGameSceneButton.position = floatArrayOf(0.3f, -0.87f, 0f)
        toGameSceneButton.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(toGameSceneButton)

        //tables
        table  =entityManager.createEntity(R.drawable.table)
        table.position = floatArrayOf(-0.16f, -0.1f, 0f)
        table.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(table)
        table  =entityManager.createEntity(R.drawable.table)
        table.position = floatArrayOf(-0.16f, -0.5f, 0f)
        table.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(table)
        table  =entityManager.createEntity(R.drawable.table)
        table.position = floatArrayOf(0.16f, -0.1f, 0f)
        table.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(table)
        table  =entityManager.createEntity(R.drawable.table)
        table.position = floatArrayOf(0.16f, -0.5f, 0f)
        table.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(table)


        //add food items
        cake =  entityManager.createEntity(R.drawable.strawberrcake)
        //cake.position = floatArrayOf(-0.325f, 0.45f, 0f)
        cake.position = floatArrayOf(-1f, -1f, 0f)
        cake.scale = floatArrayOf(0.14f, 0.14f, 0.14f)
        entities.add(cake)

        cupcake =  entityManager.createEntity(R.drawable.cupcake)
        //cupcake.position = floatArrayOf(-0.155f, 0.435f, 0f)
        cupcake.position = floatArrayOf(-1f, -1f, 0f)
        cupcake.scale = floatArrayOf(0.14f, 0.14f, 0.14f)
        entities.add(cupcake)

        latte =  entityManager.createEntity(R.drawable.latte)
        //latte.position = floatArrayOf(0.03f, 0.445f, 0f)
        latte.position = floatArrayOf(-1f, -1f, 0f)
        latte.scale = floatArrayOf(0.14f, 0.14f, 0.14f)
        entities.add(latte)

    }

    override fun onSurfaceChanged() {

    }

    private fun startCustomerSpawner() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                spawnCustomer()
                delay(3000) // Spawn every 3 seconds
            }
        }
    }

    private fun spawnCustomer() {
        val customer = entityManager.createEntity(R.drawable.placeholder_customer)
        customer.position = path[0] // Start at entry point
        customer.scale = floatArrayOf(0.2f, 0.2f, 1f)

        if (customerQueue.size >= maxCustomers) {
            // Remove first customer from queue
            val leavingCustomer = customerQueue.removeFirst()
            val leavingTablePos = customerTableMap[leavingCustomer] ?: tablePos[0] // Get their original table position
            val leavingPath = listOf(leavingTablePos, path[1], path[0])

            CoroutineScope(Dispatchers.Main).launch {
                moveEntityAlongPath(leavingCustomer, leavingPath) {
                    entities.remove(leavingCustomer) // Remove from scene after leaving
                    customerTableMap.remove(leavingCustomer) // Clean up the mapping
                }
            }
        }

        // Assign a table position dynamically
        val tableIndex = spawnIndex % maxCustomers
        val assignedTablePos = tablePos[tableIndex]
        customerTableMap[customer] = assignedTablePos // Store the table position for this customer

        val destinationPath = listOf(path[0], path[1], assignedTablePos)

        customerQueue.addLast(customer)
        spawnIndex++

        entities.add(customer)

        // Move the customer to their table
        CoroutineScope(Dispatchers.Main).launch {
            moveEntityAlongPath(customer, destinationPath, null)
        }
    }

    private suspend fun moveEntityAlongPath(entity: Entity, path: List<FloatArray>, onComplete: (() -> Unit)?) {
        for (i in 0 until path.size - 1) {
            val start = path[i]
            val end = path[i + 1]
            val duration = 1.5f
            var t = 0f

            while (t < 1f) {
                delay(16) // Approx. 60 FPS
                t += 0.0167f * (1 / duration)
                entity.position = interpolate(start, end, t)
            }

            entity.position = end
        }

        onComplete?.invoke()
    }

    private fun interpolate(start: FloatArray, end: FloatArray, t: Float): FloatArray {
        return floatArrayOf(
            start[0] + (end[0] - start[0]) * t,
            start[1] + (end[1] - start[1]) * t,
            start[2] + (end[2] - start[2]) * t
        )
    }

    override fun update() {

        if(viewModel.isFoodItemExists("cake")){
            cake.position = floatArrayOf(-0.325f, 0.45f, 0f)
        }
        if(viewModel.isFoodItemExists("cupcake")){
            cupcake.position = floatArrayOf(-0.155f, 0.435f, 0f)
        }
        if(viewModel.isFoodItemExists("latte")){
            latte.position = floatArrayOf(0.03f, 0.445f, 0f)
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
                entity.position = interpolate(start, end, t)

                // Update progress in user data
                entity.userData["progress"] = progress
            }

        }

        entityManager.setBackgroundTexture(R.drawable.shopbg)
    }

    override fun onActionDown(normalizedX: Float, normalizedY: Float) {
        viewModel.audioManager.playAudio(R.raw.click)
        synchronized(entities) {
            if(toGameSceneButton.contains(normalizedX, normalizedY)){

                sceneManager?.setScene(GameScene::class, viewModel)
            }
        }
    }

    override fun onActionMove(normalizedDx: Float, normalizedDy: Float) {
        entityManager.moveSelectedEntity(normalizedDx, normalizedDy)
    }

    override fun onActionUp() {
        entityManager.setSelectedEntity(null)
    }
}
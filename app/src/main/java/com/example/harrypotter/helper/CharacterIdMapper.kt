package com.example.harrypotter.helper

object CharacterIdMapper {
    private val simpleIdToUuid = mapOf(
        1 to "9e3f7ce4-b9a7-4244-b709-dae5c1f1d4a8",
        2 to "4c7e6819-a91a-45b2-a454-f931e4a7cce3",
        3 to "c3b1f9a5-b87b-48bf-b00d-95b093ea6390",
        4 to "ca3827f0-375a-4891-aaa5-f5e8a5bad225",
        5 to "36bfefd0-e0bb-4d11-be98-d1ef6117a77a",
        6 to "3db6dc51-b461-4fa4-a6e4-b1ff352221c5",
        7 to "1cd6dc64-01a9-4379-9cfd-1a7167ba1bb1",
        8 to "2cfd2d4b-5d1e-4dc5-8837-37a97c7e2f2f",
        9 to "b8f9095b-9de6-4d7d-83e0-4391af8f22e4",
        10 to "dd925874-e800-4eb4-9f0d-4d0fed15634b"
    )

    fun getUuid(simpleId: Int): String? {
        return simpleIdToUuid[simpleId]
    }

    fun validIds(): String {
        return simpleIdToUuid.keys.joinToString()
    }

    // Menor e maior id disponiveis (1..10, sequenciais).
    fun minId(): Int = simpleIdToUuid.keys.min()

    fun maxId(): Int = simpleIdToUuid.keys.max()
}

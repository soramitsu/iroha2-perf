package jp.co.soramitsu.load.toolbox

import jp.co.soramitsu.iroha2.*
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.generated.*
import kotlinx.coroutines.flow.Flow

class Pliers {
    lateinit var blocksResult: Flow<VersionedBlockMessage>
    lateinit var registerDomain : String
    lateinit var registerAccount : String
    lateinit var assetDefinition : String
    lateinit var assetId : String
    lateinit var blocks : MutableList<VersionedBlockMessage>
    lateinit var instructions : List<InstructionBox>

    fun getSubscribetionToBlockStream(iroha2Client: Iroha2Client, from: Long, count: Int){
        blocksResult = iroha2Client.subscribeToBlockStream(from,count)
    }
    suspend fun getLastDomain(): String{
        blocks = mutableListOf()
        blocksResult.collect { block -> blocks.add(block) }
        instructions = checkBlockStructure(blocks[blocks.size - 1])
        registerDomain = instructions[0].cast<InstructionBox.Register>().extractDomain().id.name.string
        return registerDomain
    }
    suspend fun getLastTransferAsset(): String{
        blocks = mutableListOf()
        blocksResult.collect { block -> blocks.add(block) }
        instructions = checkBlockStructure(blocks[blocks.size - 1])
        registerDomain = instructions[0].cast<InstructionBox.Transfer>().transferBox.destinationId.extractAccountId().name.string
        return registerDomain
    }

    suspend fun getLastAccount(): String{
        blocks = mutableListOf()
        blocksResult.collect { block -> blocks.add(block) }
        instructions = checkBlockStructure(blocks[blocks.size - 1])
        registerAccount = instructions[0].cast<InstructionBox.Register>().extractAccount().id.name.string
        return registerAccount
    }

    suspend fun getLastAssetDefinition(): String{
        blocks = mutableListOf()
        blocksResult.collect { block -> blocks.add(block) }
        instructions = checkBlockStructure(blocks[blocks.size - 1])
        assetDefinition = instructions[0].cast<InstructionBox.Register>().extractAssetDefinition().id.name.string
        return assetDefinition
    }

    suspend fun getLastAssetId(): String{
        blocks = mutableListOf()
        blocksResult.collect { block -> blocks.add(block) }
        instructions = checkBlockStructure(blocks[blocks.size - 1])
        assetId = instructions[0].cast<InstructionBox.Mint>().mintBox.destinationId.extractAssetId().asString()
        return assetId
    }
    private fun getCommittedBlock(versionedBlockMessage: VersionedBlockMessage): CommittedBlock {
        return versionedBlockMessage.cast<VersionedBlockMessage.V1>()
            .blockMessage.versionedCommittedBlock.cast<VersionedCommittedBlock.V1>().committedBlock
    }

    private fun getInstructionPayload(committedBlock: CommittedBlock): TransactionPayload {
        return committedBlock.transactions[0]
            .cast<VersionedValidTransaction.V1>()
            .validTransaction
            .payload
    }

    private fun checkBlockStructure(
        blockMessage: VersionedBlockMessage,
    ): List<InstructionBox> {
        val committedBlock = getCommittedBlock(blockMessage)
        val payload = getInstructionPayload(committedBlock)
        val instructions = payload.instructions.cast<Executable.Instructions>().vec
        return instructions
    }

    fun equals(actual: String, expected: String): Boolean {
        return actual.equals(expected)
    }
}
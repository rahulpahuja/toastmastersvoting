package myoo.votingapp.Model

import android.content.Context
import myoo.votingapp.R


enum class CandidateType {

    PREPARE_SPEAKER, ROLE_TAKER, EVALUTOR, TABLE_TOPIC

}

fun Int.toCandidateType(): CandidateType {
    return when(this){
        R.string.prepared_speaker -> CandidateType.PREPARE_SPEAKER
        R.string.role_takers -> CandidateType.ROLE_TAKER
        R.string.evaluators -> CandidateType.EVALUTOR
        R.string.table_topics -> CandidateType.TABLE_TOPIC
        else -> CandidateType.PREPARE_SPEAKER
    }
}

fun CandidateType.toRecourceValue(): Int {

   return when (this) {
        CandidateType.PREPARE_SPEAKER -> R.string.prepared_speaker
        CandidateType.ROLE_TAKER -> R.string.role_takers
        CandidateType.EVALUTOR -> R.string.evaluators
        CandidateType.TABLE_TOPIC -> R.string.table_topics

    }

}

fun Context.stringResource(voting_item: String?): Int {
    return when (voting_item) {
        getString(R.string.prepared_speaker) -> R.string.prepared_speaker
        getString(R.string.role_takers) -> R.string.role_takers
        getString(R.string.table_topics) -> R.string.table_topics
        getString(R.string.evaluators) -> R.string.evaluators
        else -> R.string.prepared_speaker
    }
}
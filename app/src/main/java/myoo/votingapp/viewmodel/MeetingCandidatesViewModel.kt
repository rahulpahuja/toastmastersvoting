package myoo.votingapp.viewmodel

import android.annotation.TargetApi
import android.arch.lifecycle.MutableLiveData
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.*
import io.reactivex.subjects.PublishSubject
import myoo.votingapp.Model.*
import myoo.votingapp.R
import myoo.votingapp.R.array.voting_item_list
import myoo.votingapp.R.id.view
import myoo.votingapp.Utils.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap


class MeetingCandidatesViewModel(private val reference: FirebaseReference,
                                 private val storage: Storage) : AppViewModel<List<Any>>() {

    private val hashmap = HashMap<String, Any>()

    private val counts = LinkedHashMap<String, Int>()

    val uploadingSuccessful = PublishSubject.create<Boolean>()

    val countResponse = MutableLiveData<List<Int>>()

    val progress = PublishSubject.create<Double>();


    var results = MutableLiveData<R>()

    var meetings = MutableLiveData<R>()

    fun addVotingPreparedSpeakerCandidate(detail: PreparedSpeakerDetail) {
        val reference = reference.preparedSpeaker(detail.meetingNo).pushKey(detail)


        val speakerImage = storage.prepareSpeaker().child(detail.name)

// compose : Transform a Single by applying a particular Transformer function to it.
        val subscribe = speakerImage.upload(detail.image, progress, detail.imageUrl)
                .compose(observeLoadingSingle())
                .flatMapCompletable {
                    detail.run {
                        imageUrl = it; uniqueId = reference.key ?: "";
                        reference.save(this)
                    }
                }
                .compose(observeLoadingComplete())
                .doOnComplete { uploadingSuccessful.onNext(true) }
                .subscribe()

        disposables.add(subscribe)
    }

    fun DatabaseReference.pushKey(detail: UniqueId): DatabaseReference {
        return if (detail.uniqueId.isBlank()) {
            push()
        } else {
            child(detail.uniqueId)
        }

    }

    fun saveRoleTaker(detail: RoleTakerDetail) {

        val roleTakerImage = storage.roleTaker().child(detail.name)
        val roleTaker = reference.roleTaker(detail.meetingNo).pushKey(detail)

        val subscribe = roleTakerImage.upload(detail.image, progress, detail.imageUrl ?: "")
                .compose(observeLoadingSingle())
                .flatMapCompletable {
                    detail.run {
                        imageUrl = it; uniqueId = roleTaker.key ?: "";
                        roleTaker.save(this)
                    }
                }
                .compose(observeLoadingComplete())
                .doOnComplete { uploadingSuccessful.onNext(true) }
                .subscribe()
        disposables.add(subscribe)

    }

    fun saveEvaluators(detail: EvaluatorDetail) {

        val evaluators = reference.evaluators(detail.meetingNo).pushKey(detail)
        val evalutorImage = storage.evalutaor().child(detail.name)

        val subscribe = evalutorImage.upload(detail.image, progress, detail.imageUrl ?: "")
                .compose(observeLoadingSingle())
                .flatMapCompletable {
                    detail.run {
                        imageUrl = it; uniqueId = evaluators.key ?: "";
                        evaluators.save(detail)
                    }
                }
                .doOnComplete { uploadingSuccessful.onNext(true) }
                .subscribe()

        disposables.add(subscribe)

    }

    fun clearVotes(meetingNo : String)   {

         reference.getResultsRef(meetingNo).removeValue()
                .onSuccessTask{ reference.userViseVoteForMeetingRf(meetingNo).removeValue()}

    }

    fun updateLocation(meetingId: String, location:Location){


        var date = Date();
       // val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
        val formatter = SimpleDateFormat("MMM dd yyyy")


        val todaysdate: String = formatter.format(date)
        Log.d("answer",todaysdate)

        var myref = reference.getLocationsRef().child(todaysdate).child(meetingId)

       // pushref.save(location.latitude).subscribe()
      //  pushref.save(location.longitude).subscribe()
        myref.child("latitude").save(location.latitude).subscribe()
        myref.child("longitude").save(location.longitude).subscribe()

       // pushref.save(meetingId).subscribe()

    }

    fun getListOfTodaysMeeting() : Flowable<String>{

        var date = Date();
        // val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
        val formatter = SimpleDateFormat("MMM dd yyyy")
        val todaysdate: String = formatter.format(date)


        return reference.getLocationsRef().child(todaysdate).retriveChilds<Any>()
                .map {
                    var value = it.value
                    var map = value as HashMap<String , Any>
                    it.key
                }


      //  reference.getLocationsRef().child(todaysdate).retriveList<String>().map {}

    }

    fun getLocationOfAMeeting(meetingId: String): Maybe<LatLong>{

        var date = Date();
        // val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
        val formatter = SimpleDateFormat("MMM dd yyyy")
        val todaysdate: String = formatter.format(date)

        return reference.getLocationsRef().child(todaysdate).child(meetingId).retrive()

    }


    fun getMyListOfTodaysMeetinInSingle() {

        var date = Date();
        // val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
        val formatter = SimpleDateFormat("MMM dd yyyy")
        val todaysdate: String = formatter.format(date)

         reference.getLocationsRef().child(todaysdate).retriveList<Any>()
                .map {

                   // meetings = it
                }
        //  reference.getLocationsRef().child(todaysdate).retriveList<String>().map {}

    }


    fun clearVotesAsynch(meetingNo : String) :Completable   {

        return reference.getResultsRef(meetingNo).remove()
                .andThen(  {
                    reference.userViseVoteForMeetingRf(meetingNo).remove().subscribe()
                })

    }

    fun saveTableTopic(detail: TableTopicDetail) {
        val topic = reference.tableTopic(detail.meetingNo).pushKey(detail)
        val topicImage = storage.table().child(detail.name)

        val subscribe = topicImage.upload(detail.image, progress, detail.imageUrl ?: "")
                .compose(observeLoadingSingle())
                .flatMapCompletable {
                    detail.run {
                        imageUrl = it; uniqueId = topic.key ?: "";
                        topic.save(detail)
                    }
                }
                .compose(observeLoadingComplete())
                .doOnComplete { uploadingSuccessful.onNext(true) }
                .subscribe()

        disposables.add(subscribe)

    }

    fun retriveCandidates(voting_item: CandidateType, meeting_number: String) {

        val list = when (voting_item) {
            CandidateType.PREPARE_SPEAKER -> retrivePrepareCandidate(meeting_number)
            CandidateType.TABLE_TOPIC -> retriveTableTopicCandidate(meeting_number)
            CandidateType.EVALUTOR -> retriveEvalutorCandidate(meeting_number)
            CandidateType.ROLE_TAKER -> retriveRoleTakerCandidate(meeting_number)
        }

        list.subscribe {
            response.value = it
        }

    }

    private fun retriveRoleTakerCandidate(meetingId: String): Flowable<List<Any>> {
        val roleTaker = reference.roleTaker(meetingId)

        return roleTaker.retriveChilds<RoleTakerDetail>()
                .map { evaluateAndChangeHashMap(it) }
    }

    private fun evaluateAndChangeHashMap(it: RxFirebaseChildEvent<out UniqueId>): List<Any> {

        val value = it.value

        // "when" is used as switch case/ if else ladder.
        // If compares it.eventType == RxFirebaseChildEvent.EventType.ADDED --> hashmap.put(value.uniqueId, it.value)
        when (it.eventType) {
            RxFirebaseChildEvent.EventType.ADDED -> hashmap.put(value.uniqueId, it.value)
            RxFirebaseChildEvent.EventType.REMOVED -> hashmap.remove(value.uniqueId)
            RxFirebaseChildEvent.EventType.CHANGED -> hashmap.put(value.uniqueId, it.value)
            else -> {
            }
        }

        return hashmap.values.toList()


    }
    private fun retriveEvalutorCandidate(meetingId: String): Flowable<List<Any>> {
        val evalutor = reference.evaluators(meetingId)

        return evalutor.retriveChilds<EvaluatorDetail>()
                .map {
                    evaluateAndChangeHashMap(it) }
    }

    private fun retriveTableTopicCandidate(meetingId: String): Flowable<List<Any>> {
        val tabletopic = reference.tableTopic(meetingId)

        return tabletopic.retriveChilds<TableTopicDetail>()
                .map { evaluateAndChangeHashMap(it) }
    }

    private fun retrivePrepareCandidate(meetingId: String): Flowable<List<Any>> {
        val preparedSpeaker = reference.preparedSpeaker(meetingId)

        return preparedSpeaker.retriveChilds<PreparedSpeakerDetail>()
                .map { evaluateAndChangeHashMap(it) }
    }

    fun retirveCandidateCount(meetingId: String) {


        with(reference) {
            val listOfReferences = retirveListOfAllRefrences(meetingId)

            Flowable.fromIterable(listOfReferences)
                    .map {
                        counts.put(it.key ?: "", 0)
                        it
                    }
                    .onBackpressureBuffer()
                    .flatMap {
                        val speaker = it

                        speaker.observeCount()
                    }.map {
                        counts[it.first ?: ""] = it.second
                        counts
                    }.subscribe {
                        countResponse.value = it.values.toList()

                    }

        }
    }

    private fun FirebaseReference.retirveListOfAllRefrences(meetingId: String) =
            listOf(preparedSpeaker(meetingId), roleTaker(meetingId),
                    tableTopic(meetingId), evaluators(meetingId))

    fun removeCandidate(candidate: Any, votingType: CandidateType, meetingId: String) {

        when (votingType) {
            CandidateType.PREPARE_SPEAKER -> removePrepareSpeaker(candidate as PreparedSpeakerDetail, meetingId)
            CandidateType.TABLE_TOPIC -> removetopic(candidate as TableTopicDetail, meetingId)
            CandidateType.ROLE_TAKER -> removeRoleTaker(candidate as RoleTakerDetail, meetingId)
            CandidateType.EVALUTOR -> removeEvalutor(candidate as EvaluatorDetail, meetingId)
        }

    }

    private fun removePrepareSpeaker(preparedSpeakerDetail: PreparedSpeakerDetail, meetingId: String) {

        val speaker = reference.preparedSpeaker(meetingId)
        RxFirebaseDatabase.setValue(speaker.child(preparedSpeakerDetail.uniqueId), null).subscribe()
    }

    private fun removetopic(table: TableTopicDetail, meetingId: String) {

        val speaker = reference.tableTopic(meetingId)
        RxFirebaseDatabase.setValue(speaker.child(table.uniqueId), null).subscribe()
    }

    private fun removeRoleTaker(takerDetail: RoleTakerDetail, meetingId: String) {
        val speaker = reference.roleTaker(meetingId)
        RxFirebaseDatabase.setValue(speaker.child(takerDetail.uniqueId), null).subscribe()
    }

    private fun removeEvalutor(evaluatorDetail: EvaluatorDetail, meetingId: String) {
        val speaker = reference.evaluators(meetingId)

        RxFirebaseDatabase.setValue(speaker.child(evaluatorDetail.uniqueId)
                , null).subscribe()
    }

    fun retriveAllCandidates(meeting_number: String) {

        val refrences = reference.retirveListOfAllRefrences(meeting_number)


        retriveAllCandidates(refrences).subscribe({
            response.value = it
        }, {})

    }


    // returns a single of  arrayList of candidates. Candidate can be any candidateType.
    // retrieves a list of all candidates so if there are 3 prepared speakers, 2 role takers Arralist will be of size 5
    fun retriveAllCandidates(refrences: List<DatabaseReference>): Single<ArrayList<Any>> {
        return refrences[0].retriveList<PreparedSpeakerDetail>()
                .flatMap { list ->
                    refrences[3].retriveList<EvaluatorDetail>()
                            .map { arrayListOf<Any>().apply { addAll(list);addAll(it) } }
                }
                .flatMap { list ->
                    refrences[2].retriveList<TableTopicDetail>()
                            .map { arrayListOf<Any>().apply { addAll(list);addAll(it) } }
                }
                .flatMap { list ->
                    refrences[1].retriveList<RoleTakerDetail>()
                            .map { arrayListOf<Any>().apply { addAll(list);addAll(it) } }
                }
                .compose(observeLoadingSingle())
    }


    fun submitCandidateAndVotesRequest(candidateType: CandidateType, meetingId: String): Single<HashMap<String, Any>> {

        Log.d("votescount", "In submitCandidateAndVotesRequest ")
// response is is loaded with an Arralist of all candaidates. ArraList<Any>
        return Flowable.fromIterable(response.value)
                .compose(filterByCandidatetype(candidateType))
                .flatMap { candidateE ->
                    retriveCandidateVotes(candidateE, meetingId)
                            .map {
                                Pair(candidateE, it)
                            }
                }
                .toList()
                .map {
                    hashMapOf<String, Any>().apply {
                        put("candidates", it.map { it.first })
                        put("number_of_votes", it.map { it.second })
                        put("key",candidateType)
                    }
                }
    }




    fun votesCastedByMeInAMeetingRef(meetingNo: String): DatabaseReference {

        var myref = reference.votesCastedByMeInAMeetingRef(meetingNo)


        return myref

    }


    fun getCandidates(candidateType: CandidateType, meetingId: String): Single<HashMap<String, Any>> {

        // filterByCandidatetype(candidateType) removes the disqualify candidates
        return Flowable.fromIterable(response.value)
                .compose(filterByCandidatetype(candidateType))
                .flatMap { candidateE ->
                    retriveCandidateVotes(candidateE, meetingId)
                            .map {
                                Pair(candidateE, it)
                            }
                }
                .toList()
                .map {
                    hashMapOf<String, Any>().apply {
                        put("candidates", it.map { it.first })
                        put("number_of_votes", it.map { it.second })
                        put("key",candidateType)
                    }
                }
    }

    private fun retriveCandidateVotes(it: Any, meetingId: String): Flowable<Int> {

        val ref = when (it) {
            is PreparedSpeakerDetail -> reference.updateVoteCountForPrepareSpeaker(meetingId).child(it.uniqueId)
            is RoleTakerDetail -> reference.updateVoteForRoleTaker(meetingId).child(it.uniqueId)
            is EvaluatorDetail -> reference.updateVoteForEvalutor(meetingId).child(it.uniqueId)
            is TableTopicDetail -> reference.updateVoteForTableTopic(meetingId).child(it.uniqueId)
            else -> reference.updateVoteCountForPrepareSpeaker(meetingId).child("none")
        }

        return RxFirebaseDatabase.observeSingleValueEvent(ref, Int::class.java).defaultIfEmpty(0).toFlowable()

    }

    fun filterByCandidatetype(candidate: CandidateType): FlowableTransformer<Any, Any> {

        return FlowableTransformer { emiiter ->
            return@FlowableTransformer when (candidate) {
                CandidateType.PREPARE_SPEAKER -> emiiter.filter { it is PreparedSpeakerDetail }
                CandidateType.EVALUTOR -> emiiter.filter { it is EvaluatorDetail }
                CandidateType.TABLE_TOPIC -> emiiter.filter { it is TableTopicDetail }
                CandidateType.ROLE_TAKER -> emiiter.filter { it is RoleTakerDetail }
            }
        }

        /*
        return FlowableTransformer { emiiter ->
            return@FlowableTransformer when (candidate) {
                CandidateType.PREPARE_SPEAKER -> emiiter.filter { it is PreparedSpeakerDetail && !it.isDisabled }
                CandidateType.EVALUTOR -> emiiter.filter { it is EvaluatorDetail && !it.isDisabled }
                CandidateType.TABLE_TOPIC -> emiiter.filter { it is TableTopicDetail && !it.isDisabled }
                CandidateType.ROLE_TAKER -> emiiter.filter { it is RoleTakerDetail && !it.isDisabled }
            }
        }

        */


    }

    fun giveVoteTo(voteGivenTo: Any,
                   toCandidateType: CandidateType,
                   meetingId: String) {

        when (toCandidateType) {
            CandidateType.PREPARE_SPEAKER -> giveVoteToSpeaker(voteGivenTo as PreparedSpeakerDetail, meetingId)
            CandidateType.ROLE_TAKER -> giveVoteToRoleTaker(voteGivenTo as RoleTakerDetail, meetingId)
            CandidateType.TABLE_TOPIC -> giveVoteToTopicTable(voteGivenTo as TableTopicDetail, meetingId)
            CandidateType.EVALUTOR -> giveVoteToEvalutor(voteGivenTo as EvaluatorDetail, meetingId)
        }

    }


    private fun giveVoteToTopicTable(tableTopic: TableTopicDetail, meetingId: String) {

        val voteCastedByMeForTableTopicRef = reference.voteCastedByMeForTableTopic(meetingId)
        val votesGainedByAParticularCandidateRef = reference.updateVoteForTableTopic(meetingId)
                .child(tableTopic.uniqueId)

        val votedTo = VotedTo(tableTopic.uniqueId)

        updateVote(voteCastedByMeForTableTopicRef, votedTo, votesGainedByAParticularCandidateRef)


    }

    fun updateVote(VotesCapturedRef: DatabaseReference, votedTo: VotedTo,
                   updateVoteCount: DatabaseReference) {
        RxFirebaseDatabase.setValue(VotesCapturedRef, votedTo)
                .toSingleDefault(true)
                .flatMap {
                    RxFirebaseDatabase
                            .runTransaction(updateVoteCount,
                                    1)
                }
                .subscribe()


    }

    private fun giveVoteToEvalutor(evalutorTopic: EvaluatorDetail, meetingId: String) {
        val topic = reference.voteCastedByMeForEvalutor(meetingId)
        val forTableTopic = reference.updateVoteForEvalutor(meetingId)
                .child(evalutorTopic.uniqueId)

        val votedTo = VotedTo(evalutorTopic.uniqueId)

        updateVote(topic, votedTo, forTableTopic)

    }

    private fun giveVoteToRoleTaker(roleTaker: RoleTakerDetail, meetingId: String) {
        val topic = reference.voteCastedByMeForRoleTaker(meetingId)
        val forTableTopic = reference.updateVoteForRoleTaker(meetingId)
                .child(roleTaker.uniqueId)

        val votedTo = VotedTo(roleTaker.uniqueId)

        updateVote(topic, votedTo, forTableTopic)

    }

    private fun giveVoteToSpeaker(prepaerSpeaker: PreparedSpeakerDetail, meetingId: String) {
        val topic = reference.voteCastedByMeForPreaperedSpeaker(meetingId)
        val forTableTopic = reference.updateVoteCountForPrepareSpeaker(meetingId)
                .child(prepaerSpeaker.uniqueId)

        val votedTo = VotedTo(prepaerSpeaker.uniqueId)

        updateVote(topic, votedTo, forTableTopic)


    }

    fun startVoting(meetingId: String): Single<ArrayList<CandidateType>>? {
        val refrences = reference.retirveListOfAllRefrences(meetingId)

        val alreadyAppliedRef = listOf(reference.voteCastedByMeForPreaperedSpeaker(meetingId),
                reference.voteCastedByMeForTableTopic(meetingId), reference.voteCastedByMeForEvalutor(meetingId),
                reference.voteCastedByMeForRoleTaker(meetingId))

        val pendingVotesFor = arrayListOf<CandidateType>()

        return retriveAllCandidates(refrences)
                .map { response.value = it }
                .toFlowable()
                /**flatMapIterable
                 * Returns a Flowable that merges each item emitted by the source Publisher with the values in an
                 * Iterable corresponding to that item that is generated by a selector.*/
                .flatMapIterable { alreadyAppliedRef }
                .flatMapSingle { ref ->
                    ref.isExists()
                            .map {
                                if (!it) pendingVotesFor.add(retirveType(ref.key))
                                pendingVotesFor
                            }
                }
                .last(arrayListOf())

    }

    fun initiateCandidates(meetingId: String) : Single<Unit> {

        val refrences = reference.retirveListOfAllRefrences(meetingId)

        // retriveAllCandidates retrieves a list of all candidates
        // response is loaded with that ArrayList
        return retriveAllCandidates(refrences)
                .map { response.value = it }

    }

    private fun retirveType(key: String?): CandidateType {

        return when (key) {
            reference.TABLE_TOPIC -> CandidateType.TABLE_TOPIC
            reference.ROLE_TAKER -> CandidateType.ROLE_TAKER
            reference.PREPARE_SPEAKAR -> CandidateType.PREPARE_SPEAKER
            reference.EVALUTOR -> CandidateType.EVALUTOR
            else -> CandidateType.PREPARE_SPEAKER

        }

    }


    // code to mark a candidate disqualified

    fun disqualifyTheCandidate(meetingId: String, canidate: Any) {

        when (canidate) {
            is PreparedSpeakerDetail -> disqualify(reference.preparedSpeaker(meetingId), canidate.uniqueId, canidate.isDisabled)
            is RoleTakerDetail -> disqualify(reference.roleTaker(meetingId), canidate.uniqueId, canidate.isDisabled)
            is TableTopicDetail -> disqualify(reference.tableTopic(meetingId), canidate.uniqueId, canidate.isDisabled)
            is EvaluatorDetail -> disqualify(reference.evaluators(meetingId), canidate.uniqueId, canidate.isDisabled)
        }
    }

    fun disqualify(ref: DatabaseReference, uniqueId: String, isDisabled: Boolean) {
        val disqualified = ref.child(uniqueId).child("disabled")

        RxFirebaseDatabase.setValue(disqualified, isDisabled).subscribe()
    }

    fun setMeetingActive( meetingId: String , status : Boolean): Completable {
        //val disqualified = ref.child(uniqueId).child("disabled")
        val ref = reference.isVotingActive(meetingId)
        //ref.setValue(true)
        return RxFirebaseDatabase.setValue(ref, status)
    }


    fun getMeetingPassword(meetingId: String):Maybe<String>{

        return reference.getMeetingPasswordRef(meetingId).retrive<String>()

    }


    fun getMeetingStatus( meetingId: String): Single<Boolean> {

        val ref = reference.isVotingActive(meetingId)

        return ref.isExists()

    }

    fun getMeetingStatusRef( meetingId: String): DatabaseReference {

        return reference.isVotingActive(meetingId)

    }

    fun getMeetingResultsRef( meetingId: String): DatabaseReference {

        return reference.resultOfAMeeting(meetingId)

    }





}
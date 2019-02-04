package myoo.votingapp.view.Activity

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.add_prepared_speaker.*
import myoo.votingapp.viewmodel.DBHelper
import myoo.votingapp.Model.*
import myoo.votingapp.R
import myoo.votingapp.view.Activity.PrepareAllVotingItemsScreen.Companion.MEETING
import java.io.*
import java.util.*

class AddCandidateActivity : AppCompatActivity() {
    internal val REQUEST_ADD_CANDIDATE = 100
    internal val REQUEST_EDIT_CANDIDATE = 150
    private val RESULT_CROP = 400
    internal var voting_item: String? = null
    internal var bitmap: Bitmap? = null
    internal var picUri: String = ""
    internal var uri: Uri? = null
    internal var request_code: Int = 0
    internal var position: Int = 0
    private val meeting_number by lazy {
        intent.extras.getString(MEETING)
    }
    internal var members_list: ArrayList<String>? = null
    internal var guest_list: ArrayList<String>? = null
    private val PICK_IMAGE_REQUEST = 1
    private var mFileTemp: File? = null
    private var mydb: DBHelper? = null

    //permission is automatically granted on sdk<23 upon installation
    val isWriteExternalStoragePermissionGranted: Boolean
        get() {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.v("checkit", "Permission is granted")
                    return true
                } else {

                    Log.v("checkit", "Permission is revoked")
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                    return false
                }
            } else {
                Log.v("checkit", "Permission is granted")
                return true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        if( isWriteExternalStoragePermissionGranted ){
            mydb = DBHelper(this)

            members_list = mydb!!.members
            guest_list = mydb!!.guests
            val bundle = intent.extras
            voting_item = bundle!!.getString("voting_item")
            request_code = bundle.getInt("request_code")

            if (voting_item == resources.getText(R.string.prepared_speaker))
                addPreparedSpeakerPage()
            else if (voting_item == resources.getText(R.string.role_takers))
                addRoleTakerPage()
            else if (voting_item == resources.getText(R.string.tag_team))
                addTagTeamPage()
            else if (voting_item == resources.getText(R.string.table_topics))
                addTableTopicPage()
            else if (voting_item == resources.getText(R.string.evaluators))
                addEvaluatorsPage()
            else if (voting_item == resources.getText(R.string.members))
                addMembersPage()
            else if (voting_item == resources.getText(R.string.guests))
                addGuestsPage()
            setAutoComplete()
            setTemporaryFile()

        }


        //  LinearLayout name_layout = new LinearLayout();
    }

    internal fun setAutoComplete() {
        if (voting_item == resources.getText(R.string.members))
            return
                if (voting_item == resources.getText(R.string.guests))
            return
        val textView = findViewById<View>(R.id.name_input) as AutoCompleteTextView

        val suggestion_list = ArrayList<String>()
        for (i in members_list!!.indices) {
            suggestion_list.add(members_list!![i])
        }

        for (i in guest_list!!.indices) {
            suggestion_list.add(guest_list!![i])
        }


        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestion_list)
        textView.setAdapter(adapter)
        textView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val name = textView.text.toString()
            bitmap = getBitmapMember(name)
            if (bitmap == null)
                bitmap = getBitmapGuest(name)
            (findViewById<View>(R.id.photo_of_candidate) as ImageView).setImageBitmap(bitmap)
        }
    }

    private fun addTagTeamPage() {
        setContentView(R.layout.add_tag_team)

        findViewById<View>(R.id.add_button).setOnClickListener {
            val name = (findViewById<View>(R.id.name_input) as EditText).text.toString()
            val role = (findViewById<View>(R.id.spinner) as Spinner).selectedItem.toString()

            val file = savePhotoOnDisk(bitmap, name)

            val details = TagTeamDetail(name, role, false, file);

            val intent = Intent()
            intent.putExtra("detailobject", details)


            if (request_code == REQUEST_EDIT_CANDIDATE) {
                (findViewById<View>(R.id.add_candidate_heading) as TextView).text = "Edit TagTeam's details"
                intent.putExtra("position", position)
            }


            setResult(Activity.RESULT_OK, intent)
            finish()//finishing activity
        }

        findViewById<View>(R.id.open_gallery).setOnClickListener { openGallery() }

        if (request_code == REQUEST_EDIT_CANDIDATE) {


            val bundle = intent.extras


            val detail = bundle.getSerializable("detailobject") as TagTeamDetail

            position = bundle.getInt("position")

            (findViewById<View>(R.id.name_input) as EditText).setText(detail.name)

            loadImage(detail.name, null)

            val role_index = getTagRoleindex(detail.role)

            if (role_index > -1) {
                Log.d("project_index ", "project_index was  more than  -1 ")
                (findViewById<View>(R.id.spinner) as Spinner).setSelection(role_index)
            } else
                Log.d("project_index ", "project_index was  not more than  -1 ")

            //  ((Spinner)(findViewById(R.id.spinner))).setSelection(3);


        }
    }

    private fun addPreparedSpeakerPage() {
        setContentView(R.layout.add_prepared_speaker)

        val txtinput_name = findViewById<View>(R.id.txtinput_name) as TextInputLayout
        val txtinput_title = findViewById<View>(R.id.txtinput_title) as TextInputLayout


        val txt_header = findViewById<View>(R.id.txt_header) as TextView
        val imageBackPress = findViewById<View>(R.id.imageBackPress) as ImageView

        txt_header.text = "Add prepared Speaker details"
        imageBackPress.visibility = View.VISIBLE
        imageBackPress.setOnClickListener { finish() }

        var projectCatoegoryList = ArrayList(Arrays.asList(*resources.getStringArray(R.array.projectCategory_list)))
        val projectCatoegoryListAdapter =  ArrayAdapter<String>(getApplication(), android.R.layout.simple_spinner_item, projectCatoegoryList);
        val categorySpinner = (findViewById<View>(R.id.spinnerCategory) as Spinner)
        categorySpinner.adapter = projectCatoegoryListAdapter

        var projectSpinner = (findViewById<View>(R.id.spinner) as Spinner)



        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                when (position) {
                    0 -> {
                        var mylist = ArrayList(Arrays.asList(*resources.getStringArray(R.array.CCproject_list)))
                        val dataAdapter1 =  ArrayAdapter<String>(getApplication(), android.R.layout.simple_spinner_item, mylist);
                        projectSpinner.adapter = dataAdapter1

                    }
                    1 -> {
                        var mylist = ArrayList(Arrays.asList(*resources.getStringArray(R.array.advcancedProjectList)))
                        val dataAdapter1 =  ArrayAdapter<String>(getApplication(), android.R.layout.simple_spinner_item, mylist);
                        projectSpinner.adapter = dataAdapter1

                    }

                    2 -> {
                        var mylist = ArrayList(Arrays.asList(*resources.getStringArray(R.array.pathwaysProjectList)))
                        val dataAdapter1 =  ArrayAdapter<String>(getApplication(), android.R.layout.simple_spinner_item, mylist);
                        projectSpinner.adapter = dataAdapter1

                    }
                    else -> {

                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        var mylist = ArrayList(Arrays.asList(*resources.getStringArray(R.array.project_list)))
         val dataAdapter1 =  ArrayAdapter<String>(getApplication(), android.R.layout.simple_spinner_item, mylist);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        (findViewById<View>(R.id.spinner) as Spinner).adapter = dataAdapter1




        findViewById<View>(R.id.RR_Save).setOnClickListener {
            val name = (findViewById<View>(R.id.name_input) as AutoCompleteTextView).text.toString()
            val title = (findViewById<View>(R.id.title_input) as EditText).text.toString()
            val project = (findViewById<View>(R.id.spinner) as Spinner).selectedItem.toString()

            if (name.equals("", ignoreCase = true) || title.equals("", ignoreCase = true)) {
                if (name.equals("", ignoreCase = true)) {
                    txtinput_name.isErrorEnabled = true
                    txtinput_name.error = "Name is required"
                    txtinput_name.isFocusable = true
                } else {
                    txtinput_name.isErrorEnabled = false
                }

                if (title.equals("", ignoreCase = true)) {
                    txtinput_title.isErrorEnabled = true
                    txtinput_title.error = "Title is required"
                    txtinput_title.isFocusable = true
                } else {
                    txtinput_title.isErrorEnabled = false
                }
            } else {
                txtinput_name.isFocusable = true
                txtinput_title.isErrorEnabled = false

                val onDisk = savePhotoOnDisk(bitmap, name)

                val detail = if (request_code == REQUEST_EDIT_CANDIDATE) {
                    val bundle = intent.extras
                    bundle.getParcelable<Parcelable>("detailobject") as PreparedSpeakerDetail

                } else {
                    PreparedSpeakerDetail(name, title, project, false, onDisk)
                }

                with(detail) {
                    if (onDisk != null) {
                        image = onDisk
                    }
                }

                detail.name = name
                detail.title = title
                detail.project = project

                val intent = Intent()
                intent.putExtra("detailobject", detail)


                if (request_code == REQUEST_EDIT_CANDIDATE) {
                    intent.putExtra("position", position)
                }


                setResult(Activity.RESULT_OK, intent)
                finish()//finishing activity
            }
        }

        findViewById<View>(R.id.open_gallery).setOnClickListener { openGallery() }

        if (request_code == REQUEST_EDIT_CANDIDATE) {


            val bundle = intent.extras
            val detail = bundle.getParcelable<Parcelable>("detailobject") as PreparedSpeakerDetail

            position = bundle.getInt("position")

            (findViewById<View>(R.id.name_input) as EditText).setText(detail.name)
            (findViewById<View>(R.id.title_input) as EditText).setText(detail.title)

            loadImage(detail.name, detail.imageUrl)

            val project_index = getProjectIndex(detail.project)

            if (project_index > -1) {
                Log.d("project_index ", "project_index was  more than  -1 ")
                (findViewById<View>(R.id.spinner) as Spinner).setSelection(project_index)
            } else
                Log.d("project_index ", "project_index was  not more than  -1 ")

        }

    }

    private fun addRoleTakerPage() {

        setContentView(R.layout.add_role_taker)

        val txtinput_name = findViewById<View>(R.id.txtinput_name) as TextInputLayout

        val txt_header = findViewById<View>(R.id.txt_header) as TextView
        val imageBackPress = findViewById<View>(R.id.imageBackPress) as ImageView

        txt_header.text = "Add Role Taker details"
        imageBackPress.visibility = View.VISIBLE
        imageBackPress.setOnClickListener { finish() }


        findViewById<View>(R.id.RR_Save).setOnClickListener {
            val name = (findViewById<View>(R.id.name_input) as EditText).text.toString()
            val role = (findViewById<View>(R.id.spinner) as Spinner).selectedItem.toString()

            if (name.equals("", ignoreCase = true)) {
                if (name.equals("", ignoreCase = true)) {
                    txtinput_name.isErrorEnabled = true
                    txtinput_name.error = "Name is required"
                    txtinput_name.isFocusable = true
                } else {
                    txtinput_name.isErrorEnabled = false
                }


            } else {

                txtinput_name.isErrorEnabled = false


                val image = savePhotoOnDisk(bitmap, name)


                val details = if (request_code != REQUEST_EDIT_CANDIDATE) {
                    RoleTakerDetail(name, role, false, image)
                } else {
                    val bundle = intent.extras
                    bundle.getParcelable<Parcelable>("detailobject") as RoleTakerDetail
                }

                details.name = name
                if (image != null) details.image = image

                val intent = Intent()
                intent.putExtra("detailobject", details)

                if (request_code == REQUEST_EDIT_CANDIDATE) {
                    intent.putExtra("position", position)
                }


                setResult(Activity.RESULT_OK, intent)
                finish()//finishing activity
            }
        }

        findViewById<View>(R.id.open_gallery).setOnClickListener { openGallery() }

        if (request_code == REQUEST_EDIT_CANDIDATE) {


            val bundle = intent.extras


            val detail = bundle.getParcelable<Parcelable>("detailobject") as RoleTakerDetail

            position = bundle.getInt("position")

            (findViewById<View>(R.id.name_input) as EditText).setText(detail.name)

            loadImage(detail.name, detail.imageUrl)

            val role_index = getRoleindex(detail.role)

            if (role_index > -1) {
                Log.d("project_index ", "project_index was  more than  -1 ")
                (findViewById<View>(R.id.spinner) as Spinner).setSelection(role_index)
            } else
                Log.d("project_index ", "project_index was  not more than  -1 ")

            //  ((Spinner)(findViewById(R.id.spinner))).setSelection(3);


        }
    }

    private fun addTableTopicPage() {
        setContentView(R.layout.activity_add_candidate)

        val txt_header = findViewById<View>(R.id.txt_header) as TextView
        val imageBackPress = findViewById<View>(R.id.imageBackPress) as ImageView

        txt_header.text = "Add table topic speaker's details"
        imageBackPress.visibility = View.VISIBLE
        imageBackPress.setOnClickListener { finish() }

        val txtinput_name = findViewById<View>(R.id.txtinput_name) as TextInputLayout


        findViewById<View>(R.id.RR_Save).setOnClickListener {
            val name = (findViewById<View>(R.id.name_input) as EditText).text.toString()

            if (name.equals("", ignoreCase = true)) {
                if (name.equals("", ignoreCase = true)) {
                    txtinput_name.isErrorEnabled = true
                    txtinput_name.error = "Name is required"
                    txtinput_name.isFocusable = true
                } else {
                    txtinput_name.isErrorEnabled = false
                }


            } else {

                val image = savePhotoOnDisk(bitmap, name)

                val details = if (request_code != REQUEST_EDIT_CANDIDATE) {
                    TableTopicDetail(name, false, image, meeting_number)
                } else {
                    val bundle = intent.extras
                    bundle.getParcelable<Parcelable>("detailobject") as TableTopicDetail
                }

                image.let { details.image = it }
                details.name = name


                val intent = Intent()
                intent.putExtra("detailobject", details)


                if (request_code == REQUEST_EDIT_CANDIDATE) {
                    intent.putExtra("position", position)
                }



                setResult(Activity.RESULT_OK, intent)
                finish()//finishing activity
            }
        }

        findViewById<View>(R.id.open_gallery).setOnClickListener { openGallery() }

        if (request_code == REQUEST_EDIT_CANDIDATE) {


            val bundle = intent.extras


            val detail = bundle.getParcelable<Parcelable>("detailobject") as TableTopicDetail

            position = bundle.getInt("position")

            val name = detail.name
            (findViewById<View>(R.id.name_input) as EditText).setText(name)

            loadImage(name, detail.imageUrl)


        }

    }

    private fun addEvaluatorsPage() {

        setContentView(R.layout.activity_add_candidate)

        val txt_header = findViewById<View>(R.id.txt_header) as TextView
        val imageBackPress = findViewById<View>(R.id.imageBackPress) as ImageView

        txt_header.text = "Add evaluator's details"
        imageBackPress.visibility = View.VISIBLE
        imageBackPress.setOnClickListener { finish() }


        val txtinput_name = findViewById<View>(R.id.txtinput_name) as TextInputLayout


        findViewById<View>(R.id.RR_Save).setOnClickListener {
            val name = (findViewById<View>(R.id.name_input) as EditText).text.toString()

            if (name.equals("", ignoreCase = true)) {
                if (name.equals("", ignoreCase = true)) {
                    txtinput_name.isErrorEnabled = true
                    txtinput_name.error = "Name is required"
                    txtinput_name.isFocusable = true
                } else {
                    txtinput_name.isErrorEnabled = false
                }


            } else {


                val savePhotoOnDisk = savePhotoOnDisk(bitmap, name)

                val bundle = intent.extras

                val details =   if (request_code == REQUEST_EDIT_CANDIDATE) {
                  bundle.getParcelable<Parcelable>("detailobject") as EvaluatorDetail
                }else{
                    EvaluatorDetail(name, false, savePhotoOnDisk)
                }

                if(savePhotoOnDisk!=null){
                    details.image = savePhotoOnDisk
                }

                details.name  = name


                val intent = Intent()
                intent.putExtra("detailobject", details)


                if (request_code == REQUEST_EDIT_CANDIDATE) {
                    intent.putExtra("position", position)
                }

                setResult(Activity.RESULT_OK, intent)
                finish()//finishing activity
            }
        }

        findViewById<View>(R.id.open_gallery).setOnClickListener { openGallery() }

        if (request_code == REQUEST_EDIT_CANDIDATE) {


            val bundle = intent.extras


            val detail = bundle.getParcelable<Parcelable>("detailobject") as EvaluatorDetail

            position = bundle.getInt("position")

            (findViewById<View>(R.id.name_input) as EditText).setText(detail.name)

            loadImage(detail.name, detail.imageUrl)


        }

    }

    private fun loadImage(name: String, imageUrl: String?) {

        bitmap = getBitmap(name)
        if (bitmap != null) photo_of_candidate.setImageBitmap(bitmap)

        if (imageUrl!=null){
            Glide.with(photo_of_candidate).load(imageUrl).into(photo_of_candidate)
        }
    }

    private fun addMembersPage() {

        setContentView(R.layout.activity_add_candidate)

        val txt_header = findViewById<View>(R.id.txt_header) as TextView
        val imageBackPress = findViewById<View>(R.id.imageBackPress) as ImageView

        txt_header.text = "Add member's details"
        imageBackPress.visibility = View.VISIBLE
        imageBackPress.setOnClickListener { finish() }


        val txtinput_name = findViewById<View>(R.id.txtinput_name) as TextInputLayout


        findViewById<View>(R.id.RR_Save).setOnClickListener {
            val name = (findViewById<View>(R.id.name_input) as EditText).text.toString()

            if (name.equals("", ignoreCase = true)) {
                if (name.equals("", ignoreCase = true)) {
                    txtinput_name.isErrorEnabled = true
                    txtinput_name.error = "Name is required"
                    txtinput_name.isFocusable = true
                } else {
                    txtinput_name.isErrorEnabled = false
                }


            } else {

                val intent = Intent()
                intent.putExtra("detailobject", name)


                if (request_code == REQUEST_EDIT_CANDIDATE) {
                    intent.putExtra("position", position)
                }

                if (bitmap != null)
                    saveMemberPhotoOnDisk(bitmap, name)
                setResult(Activity.RESULT_OK, intent)
                finish()//finishing activity
            }
        }

        findViewById<View>(R.id.open_gallery).setOnClickListener { openGallery() }

        if (request_code == REQUEST_EDIT_CANDIDATE) {


            val bundle = intent.extras
            position = bundle!!.getInt("position")

            val name = bundle.getString("detailobject")


            (findViewById<View>(R.id.name_input) as EditText).setText(name)

            bitmap = getBitmapMember(name)
            (findViewById<View>(R.id.photo_of_candidate) as ImageView).setImageBitmap(bitmap)


        }

    }

    private fun addGuestsPage() {

        setContentView(R.layout.activity_add_candidate)

        val txt_header = findViewById<View>(R.id.txt_header) as TextView
        val imageBackPress = findViewById<View>(R.id.imageBackPress) as ImageView

        txt_header.text = "Add Guest's  details"
        imageBackPress.visibility = View.VISIBLE
        imageBackPress.setOnClickListener { finish() }


        val txtinput_name = findViewById<View>(R.id.txtinput_name) as TextInputLayout


        findViewById<View>(R.id.RR_Save).setOnClickListener {
            val name = (findViewById<View>(R.id.name_input) as EditText).text.toString()

            if (name.equals("", ignoreCase = true)) {
                if (name.equals("", ignoreCase = true)) {
                    txtinput_name.isErrorEnabled = true
                    txtinput_name.error = "Name is required"
                    txtinput_name.isFocusable = true
                } else {
                    txtinput_name.isErrorEnabled = false
                }


            } else {
                val intent = Intent()
                intent.putExtra("detailobject", name)


                if (request_code == REQUEST_EDIT_CANDIDATE) {
                    intent.putExtra("position", position)
                }

                if (bitmap != null)
                    saveGuestPhotoOnDisk(bitmap, name)
                setResult(Activity.RESULT_OK, intent)
                finish()//finishing activity
            }
        }

        findViewById<View>(R.id.open_gallery).setOnClickListener { openGallery() }

        if (request_code == REQUEST_EDIT_CANDIDATE) {


            val bundle = intent.extras
            position = bundle!!.getInt("position")

            val name = bundle.getString("detailobject")
            (findViewById<View>(R.id.name_input) as EditText).setText(name)

            bitmap = getBitmapGuest(name)
            (findViewById<View>(R.id.photo_of_candidate) as ImageView).setImageBitmap(bitmap)


        }

    }

    private fun getProjectIndex(project: String): Int {

        val list_of_projects = resources.getStringArray(R.array.project_list)

        for (i in list_of_projects.indices) {
            if (list_of_projects[i] == project) return i
        }

        return -1
    }

    private fun getRoleindex(role: String): Int {

        val list_of_roles = resources.getStringArray(R.array.role_list)

        for (i in list_of_roles.indices) {
            if (list_of_roles[i] == role) return i
        }

        return -1
    }

    private fun getTagRoleindex(role: String): Int {

        val list_of_roles = resources.getStringArray(R.array.tag_role_list)

        for (i in list_of_roles.indices) {
            if (list_of_roles[i] == role) return i
        }

        return -1
    }

    internal fun getBitmap(name: String): Bitmap? {

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/voting app /meeting $meeting_number/$voting_item")

        if (myDir.exists())
            Log.d("findphoto", "myDir.exists() is true ")
        Log.d("findphoto", "myDir is  " + myDir.toString())

        val fname = "$name.jpg"
        val file = File(myDir, fname)

        Log.d("findphoto", "file is  " + file.toString())
        val bitmap: Bitmap
        if (file.exists()) {
            Log.d("findphoto", "file.exists() is true ")
            bitmap = BitmapFactory.decodeFile(file.absolutePath)

            return bitmap
        }
        Log.d("findphoto", "file.exists() is false ")
        return null
    }

    internal fun getBitmapMember(name: String?): Bitmap? {

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/voting app /members")

        if (myDir.exists())
            Log.d("findphoto", "myDir.exists() is true ")
        Log.d("findphoto", "myDir is  " + myDir.toString())

        val fname = name!! + ".jpg"
        val file = File(myDir, fname)

        Log.d("findphoto", "file is  " + file.toString())
        val bitmap: Bitmap
        if (file.exists()) {
            Log.d("findphoto", "file.exists() is true ")
            bitmap = BitmapFactory.decodeFile(file.absolutePath)

            return bitmap
        }
        Log.d("findphoto", "file.exists() is false ")
        return null
    }

    internal fun getBitmapGuest(name: String?): Bitmap? {

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/voting app /guests")

        if (myDir.exists())
            Log.d("findphoto", "myDir.exists() is true ")
        Log.d("findphoto", "myDir is  " + myDir.toString())

        val fname = name!! + ".jpg"
        val file = File(myDir, fname)

        Log.d("findphoto", "file is  " + file.toString())
        val bitmap: Bitmap
        if (file.exists()) {
            Log.d("findphoto", "file.exists() is true ")
            bitmap = BitmapFactory.decodeFile(file.absolutePath)

            return bitmap
        }
        Log.d("findphoto", "file.exists() is false ")
        return null
    }

    internal fun findPhotoFromClubDB(name: String): Bitmap? {

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/voting app /club/WIPRO BEATS")

        if (myDir.exists())
            Log.d("findphoto", "myDir.exists() is true ")
        Log.d("findphoto", "myDir is  " + myDir.toString())

        val fname = "$name.jpg"
        val file = File(myDir, fname)

        Log.d("findphoto", "file is  " + file.toString())
        val bitmap: Bitmap
        if (file.exists()) {
            Log.d("findphoto", "file.exists() is true ")
            bitmap = BitmapFactory.decodeFile(file.absolutePath)

            return bitmap
        }
        Log.d("findphoto", "file.exists() is false ")
        return null
    }

    fun pickAnImage(v: View) {
        val intent = Intent()
        // Show only images, no videos or anything else
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        // Always editCreateDBBackup the chooser (if there are multiple options available)
        // startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            Log.e("checkcrop", "got pick image request result")
            uri = data.data

            picUri = getPicturePath(uri)
            performCrop(picUri)

            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                // Log.d(TAG, String.valueOf(bitmap));

                val photo_of_candidate = findViewById<View>(R.id.photo_of_candidate) as ImageView
                photo_of_candidate.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        if (requestCode == RESULT_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                val extras = data!!.extras
                bitmap = extras!!.getParcelable("data")
                // Set The Bitmap Data To ImageView

                val photo_of_candidate = findViewById<View>(R.id.photo_of_candidate) as ImageView
                photo_of_candidate.setImageBitmap(bitmap)
                photo_of_candidate.scaleType = ImageView.ScaleType.FIT_XY
            }
        }


        if (requestCode == REQUEST_CODE_GALLERY) {

            try {

                val inputStream = contentResolver.openInputStream(data!!.data!!)
                val fileOutputStream = FileOutputStream(mFileTemp!!)
                copyStream(inputStream, fileOutputStream)
                fileOutputStream.close()
                inputStream!!.close()

                startCropImage()

            } catch (e: Exception) {

                Log.e(TAG, "Error while creating temp file", e)
            }

        }

        if (requestCode == REQUEST_CODE_CROP_IMAGE) {

            if (resultCode == Activity.RESULT_OK) {

                val path = data!!.getStringExtra(CropImageActivity.IMAGE_PATH) ?: return

                bitmap = BitmapFactory.decodeFile(mFileTemp!!.path)
                val photo_of_candidate = findViewById<View>(R.id.photo_of_candidate) as ImageView
                photo_of_candidate.setImageBitmap(bitmap)
                photo_of_candidate.scaleType = ImageView.ScaleType.FIT_XY
            }
        }


    }

    internal fun setTemporaryFile() {

        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            mFileTemp = File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME)
        } else {
            mFileTemp = File(filesDir, TEMP_PHOTO_FILE_NAME)
        }
    }

    private fun openGallery() {

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY)

        val intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    private fun startCropImage() {

        val intent = Intent(this, CropImageActivity::class.java)
        intent.putExtra(CropImageActivity.IMAGE_PATH, mFileTemp!!.path)
        intent.putExtra(CropImageActivity.SCALE, true)

        intent.putExtra(CropImageActivity.ASPECT_X, 3)
        intent.putExtra(CropImageActivity.ASPECT_Y, 3)

        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE)
    }

    private fun getPicturePath(selectedImage: Uri?): String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        // Queries the content provider and getting result in form of cursor.
        /* parameters: a)  The columns to return for each row
                                    b) Selection criteria
                          c) Selection criteria
                        d) The sort order for the returned rows  */
        val cursor = contentResolver.query(selectedImage,
                filePathColumn, null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()

        return picturePath
    }


    fun performCrop(picUri: String?) {

        if (picUri == null)
            return

        try {
            //Start Crop Activity

            val cropIntent = Intent("com.android.camera.action.CROP")
            // indicate image type and Uri
            val f = File(picUri)
            val contentUri = Uri.fromFile(f)

            cropIntent.setDataAndType(uri, "image/*")

            cropIntent.putExtra("BitmapImage", bitmap)

            // set crop properties
            cropIntent.putExtra("crop", "true")
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)

            // indicate output X and Y
            cropIntent.putExtra("outputX", 380)
            cropIntent.putExtra("outputY", 380)

            // retrieve data on return
            cropIntent.putExtra("return-data", true)
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP)
        } catch (anfe: ActivityNotFoundException) {
            // display an error message
            val errorMessage = "your device doesn't support the crop action!"
            val toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
            toast.show()
        }
        // respond to users whose devices do not support the crop action
    }

    private fun savePhotoOnDisk(bitmap: Bitmap?, name: String): File? {
        Log.d("savephoto", "in savePhotoOnDisk ")

        if (bitmap != null) {

            //   File file = new File(getExternalFilesDir(null), "meeting "+meeting_number +".xls");

            val root = Environment.getExternalStorageDirectory().toString()
            Log.d("savephoto", "root is $root")
            val myDir = File("$root/voting app/meeting$meeting_number/$voting_item")
            myDir.mkdirs()

            val fname = "$name.jpg"
            val file = File(myDir, fname)
            Log.d("savephoto", "" + file)
            if (file.exists())
                file.delete()
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file
        } else {
            return null
        }
    }


    private fun saveMemberPhotoOnDisk(bitmap: Bitmap?, name: String) {
        Log.d("savephoto", "in savePhotoOnDisk ")

        if (bitmap != null) {
            val root = Environment.getExternalStorageDirectory().toString()
            Log.d("savephoto", "root is $root")
            val myDir = File("$root/voting app /members")
            myDir.mkdirs()

            val fname = "$name.jpg"
            val file = File(myDir, fname)
            Log.d("savephoto", "" + file)
            if (file.exists())
                file.delete()
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun saveGuestPhotoOnDisk(bitmap: Bitmap?, name: String) {
        Log.d("savephoto", "in savePhotoOnDisk ")

        if (bitmap != null) {

            val root = Environment.getExternalStorageDirectory().toString()
            Log.d("savephoto", "root is $root")
            val myDir = File("$root/voting app /guests")
            myDir.mkdirs()

            val fname = "$name.jpg"
            val file = File(myDir, fname)
            Log.d("savephoto", "" + file)
            if (file.exists())
                file.delete()
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    companion object {

        val REQUEST_CODE_GALLERY = 10
        val REQUEST_CODE_TAKE_PICTURE = 20
        val REQUEST_CODE_CROP_IMAGE = 30
        val TAG = "MainActivity"
        val TEMP_PHOTO_FILE_NAME = "temp_photo.jpg"

        @Throws(IOException::class)
        fun copyStream(input: InputStream?, output: OutputStream) {

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (true) {
                bytesRead = input!!.read(buffer)
                if (bytesRead == -1) break
                output.write(buffer, 0, bytesRead)
            }
        }
    }
}




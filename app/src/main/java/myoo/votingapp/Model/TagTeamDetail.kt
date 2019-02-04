package myoo.votingapp.Model

import java.io.File
import java.io.Serializable


class TagTeamDetail(var name: String,
                    var role: String,
                    var isDisabled: Boolean,
                    val image: File?=null) : Serializable
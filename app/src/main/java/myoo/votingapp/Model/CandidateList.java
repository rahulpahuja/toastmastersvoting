package myoo.votingapp.Model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;



public class CandidateList implements Serializable {

    public ArrayList<Candidate> candidateArrayLista;
    String voting_item;

    public ArrayList<Candidate> getCandidateArrayLista() {
        return candidateArrayLista;
    }

    public void setCandidateArrayLista(ArrayList<Candidate> candidateArrayLista) {
        this.candidateArrayLista = candidateArrayLista;
    }


    public String getVoting_item() {
        return voting_item;
    }

    public void setVoting_item(String voting_item) {
        this.voting_item = voting_item;
    }
}
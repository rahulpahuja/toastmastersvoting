package myoo.votingapp.Model;

import android.graphics.Bitmap;

import java.io.Serializable;



public class Candidate implements Serializable {
    public String name, project, title, role, voting_item;
    public String photo;
    public boolean isDisabled;
    int positionOfCandidate;

    public int getPositionOfCandidate() {
        return positionOfCandidate;
    }

    public void setPositionOfCandidate(int positionOfCandidate) {
        this.positionOfCandidate = positionOfCandidate;
    }

    public boolean getisDisabled() {
        return isDisabled;
    }

    public void setisDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVoting_item() {
        return voting_item;
    }

    public void setVoting_item(String voting_item) {
        this.voting_item = voting_item;
    }

}
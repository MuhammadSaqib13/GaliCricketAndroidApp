package com.example.galicricket.TeamDetails;

public class TeamInfo {
    private String teamName;
    private long playersCount;
    private boolean isSelected;

    public TeamInfo(String teamName) {
        this.teamName = teamName;
        this.isSelected = false;
    }

    public TeamInfo(String teamName, long playersCount) {
        this.teamName = teamName;
        this.playersCount = playersCount;
    }

//    public TeamInfo() {
//
//    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTeamName() {
        return teamName;
    }

    public long getPlayersCount() {
        return playersCount;
    }
}

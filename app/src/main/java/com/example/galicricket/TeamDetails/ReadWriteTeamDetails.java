package com.example.galicricket.TeamDetails;

public class ReadWriteTeamDetails {

    String UserId,PlayerName, BattingStyle, BowlingStyle;

    public ReadWriteTeamDetails() {
    }

    public ReadWriteTeamDetails(String playerName) {
        PlayerName = playerName;
    }

    public ReadWriteTeamDetails(String userId, String playerName, String battingStyle, String bowlingStyle) {
        UserId = userId;
        PlayerName = playerName;
        BattingStyle = battingStyle;
        BowlingStyle = bowlingStyle;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        PlayerName = playerName;
    }

    public String getBattingStyle() {
        return BattingStyle;
    }

    public void setBattingStyle(String battingStyle) {
        BattingStyle = battingStyle;
    }

    public String getBowlingStyle() {
        return BowlingStyle;
    }

    public void setBowlingStyle(String bowlingStyle) {
        BowlingStyle = bowlingStyle;
    }
}

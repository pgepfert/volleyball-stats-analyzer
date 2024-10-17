package model.database.dao;

import model.database.entity.Team;

import java.util.List;

public interface TeamDao {

    long insertTeam(Team team);

    long getTeamId(String teamName, String season);

    Team getTeam(long id);

    List<Team> getAllTeams();
}

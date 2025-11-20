package net.normalv.pathing;

import net.normalv.pathing.goal.Goal;

import java.util.ArrayList;
import java.util.List;

public class PathFinder {
    private List<Goal> goals = new ArrayList<>();

    public void update(){
        goals.forEach(Goal::update);
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }
}

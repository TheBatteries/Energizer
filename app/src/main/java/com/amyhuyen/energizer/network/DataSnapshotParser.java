package com.amyhuyen.energizer.network;

import com.amyhuyen.energizer.DBKeys;
import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.models.Cause;
import com.amyhuyen.energizer.models.Skill;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataSnapshotParser {

    protected static List<Skill> parseSkills(DataSnapshot dataSnapshot) {
        Map<String, Object> skillsMap = (Map<String, Object>) dataSnapshot.getValue();
        ArrayList<Skill> skills = new ArrayList<>();

        for (Map.Entry<String, Object> entry : skillsMap.entrySet()) {
            Map skillMap = (Map) entry.getValue();
            Skill skill = new Skill((String) skillMap.get(DBKeys.KEY_SKILL_INNER));
            skills.add(skill);
        }
        return skills;
    }

    protected static List<Opportunity> parseOpportunities(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        ArrayList<Opportunity> opportunities = new ArrayList<>();

        // iterate through children to get each opportunity and add it to newOpportunities
        for (DataSnapshot child : children) {
            Opportunity newOpp = child.getValue(Opportunity.class);
            opportunities.add(newOpp);
        }
        return skills;
    }

    protected static List<Cause> parseUserCauses(DataSnapshot dataSnapshot){
        Map<String, Object> causesPerUserMap = (Map<String, Object>) dataSnapshot.getValue();
        ArrayList<Cause> userCauses = new ArrayList<>();

        for (Map.Entry<String, Object> entry : causesPerUserMap.entrySet()) {
            Map causePerUserMap = (Map) entry.getValue();
            Cause cause = new Cause( (String) causePerUserMap.get(DBKeys.KEY_SKILL_ID));
            userCauses.add(cause);
        }
        return userCauses;
    }
}

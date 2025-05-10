package akira.riot.dto;

import java.util.List;
import java.util.Map;

public class ChampionMasteryDto {
    public String puuid;
    public int championId;  // 챔프 ID
    public int championLevel;   // 숙련도 레벨
    public int championPoints;  // 숙련도 점수
    public long lastPlayTime;   // 마지막 플레이 시간
    public int championPointsSinceLastLevel;
    public int championPointsUntilNextLevel;
    public int markRequiredForNextLevel;
    public int tokensEarned;
    public int championSeasonMilestone;
    public List<String> milestoneGrades;
    public NextSeasonMilestone nextSeasonMilestone; // 다음 시즌 업적 조건 정보

    public static class NextSeasonMilestone {
        public Map<String, Integer> requireGradeCounts;
        public int rewardMarks;
        public boolean bonus;
        public int totalGamesRequires;
    }
}

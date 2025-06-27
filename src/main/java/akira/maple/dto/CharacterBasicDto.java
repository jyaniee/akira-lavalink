package akira.maple.dto;

public class CharacterBasicDto {
    private String date;
    private String character_name;
    private String world_name;
    private String character_gender;
    private String character_class;
    private String character_class_level;
    private int character_level;
    private long character_exp;
    private String character_exp_rate;
    private String character_guild_name;
    private String character_image;
    private String character_date_create;
    private String access_flag;
    private String liberation_quest_clear_flag;

    // Getters
    public String getDate() {
        return date;
    }

    public String getCharacterName() {
        return character_name;
    }

    public String getWorldName() {
        return world_name;
    }

    public String getCharacterGender() {
        return character_gender;
    }

    public String getCharacterClass() {
        return character_class;
    }

    public String getCharacterClassLevel() {
        return character_class_level;
    }

    public int getCharacterLevel() {
        return character_level;
    }

    public long getCharacterExp() {
        return character_exp;
    }

    public String getCharacterExpRate() {
        return character_exp_rate;
    }

    public String getCharacterGuildName() {
        return character_guild_name;
    }

    public String getCharacterImage() {
        return character_image;
    }

    public String getCharacterDateCreate() {
        return character_date_create;
    }

    public String getAccessFlag() {
        return access_flag;
    }

    public String getLiberationQuestClearFlag() {
        return liberation_quest_clear_flag;
    }

}


package hu.bmiklos.bc.service;

public enum ParticipantInfoType {
    EMAIL_ADDRESS, EXTERNAL_ID, FREE_TEXT;

    public static ParticipantInfoType from(String participantInfo) {
        if (isEmailAddress(participantInfo)) {
            return EMAIL_ADDRESS;
        }
        if (isNumber(participantInfo)) {
            return EXTERNAL_ID;
        }
        return FREE_TEXT;
    }

    private static boolean isEmailAddress(String participantInfo) {
        return participantInfo.contains("@");
    }

    private static boolean isNumber(String participantInfo) {
        return participantInfo.chars()
            .allMatch(Character::isDigit);
    }

}

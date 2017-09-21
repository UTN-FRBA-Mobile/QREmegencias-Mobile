package com.qre.utils;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class QRUtils {

    public static class DTO {
        private char sex;
        private String bloodType;
        private int age;
        private List<String> allergies = new ArrayList<>();
        private List<String> pathologies = new ArrayList<>();
        private String url;
        private String contactName;
        private String contactPhone;

        @Override
        public String toString() {
            return "DTO{" +
                    "sex=" + sex +
                    ", bloodType='" + bloodType + '\'' +
                    ", age=" + age +
                    ", allergies=" + allergies +
                    ", pathologies=" + pathologies +
                    ", url='" + url + '\'' +
                    ", contactName='" + contactName + '\'' +
                    ", contactPhone='" + contactPhone + '\'' +
                    '}';
        }

        public char getSex() {
            return sex;
        }

        public void setSex(char sex) {
            this.sex = sex;
        }

        public String getBloodType() {
            return bloodType;
        }

        public void setBloodType(String bloodType) {
            this.bloodType = bloodType;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public List<String> getAllergies() {
            return allergies;
        }

        public List<String> getPathologies() {
            return pathologies;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }
    }

    private static final String CHARSET_NAME = "ISO-8859-1";

    public static DTO parseQR(final byte[] bytes)
            throws UnsupportedEncodingException {

        final ByteBuffer yearSexBloodBuffer = ByteBuffer.allocate(2).put(bytes, 0, 2);
        byte sex = (byte) ((yearSexBloodBuffer.get(0) & 0b00011000) >> 3);
        byte bloodType = (byte) ((yearSexBloodBuffer.get(0) & 0b11100000) >> 5);
        yearSexBloodBuffer.rewind();
        short bithdateYear = (short) (yearSexBloodBuffer.getShort() & 0b0000011111111111);

        final DTO dto = new DTO();
        dto.setSex(getSex(sex));
        dto.setBloodType(getBlood(bloodType));
        dto.setAge(LocalDate.now().getYear() - bithdateYear);
        setAllergiesAndPathologies(bytes, dto);

        byte urlLength = bytes[3];
        dto.setUrl(new String(bytes, 4, urlLength, CHARSET_NAME));

        int urlEnd = 4 + urlLength;
        if (bytes.length > urlEnd) {
            byte nameLength = bytes[urlEnd + 1];
            dto.setContactName(new String(bytes, urlEnd + 2, nameLength, CHARSET_NAME));
            int nameEnd = urlEnd + 2 + nameLength;

            byte phoneLength = bytes[nameEnd + 1];
            dto.setContactPhone(new String(bytes, nameEnd + 2, phoneLength, CHARSET_NAME));
        }

        return dto;

    }

    private static void setAllergiesAndPathologies(final byte[] content, final DTO dto) {
        final BitSet bitSet = BitSet.valueOf(content);
        if (bitSet.get(16)) {
            dto.getAllergies().add("Penicilina");
        }
        if (bitSet.get(17)) {
            dto.getAllergies().add("Insulina");
        }
        if (bitSet.get(18)) {
            dto.getAllergies().add("Rayos X con yodo");
        }
        if (bitSet.get(19)) {
            dto.getAllergies().add("Sulfamidas");
        }

        if (bitSet.get(20)) {
            dto.getPathologies().add("Hipertension");
        }
        if (bitSet.get(21)) {
            dto.getPathologies().add("Asma");
        }
        if (bitSet.get(22)) {
            dto.getPathologies().add("Antecedentes Oncologicos");
        }
        if (bitSet.get(23)) {
            dto.getPathologies().add("Insuficiencia Suprarrenal");
        }

    }

    private static String getBlood(final byte blood) {
        switch (blood) {
            case 0b000:
                return "0-";
            case 0b001:
                return "0+";
            case 0b010:
                return "A-";
            case 0b011:
                return "A+";
            case 0b100:
                return "B-";
            case 0b101:
                return "B+";
            case 0b110:
                return "AB-";
            case 0b111:
                return "AB+";
            default:
                return "";
        }
    }

    private static char getSex(final byte sex) {
        switch (sex) {
            case 0b00:
                return 'M';
            case 0b01:
                return 'F';
            default:
                return 'O';
        }
    }


}

package com.qre.utils;

import com.qre.models.EmergencyData;

import org.threeten.bp.LocalDate;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class QRUtils {

    private static final String CHARSET_NAME = "ISO-8859-1";

    public static EmergencyData parseQR(final byte[] bytes)
            throws UnsupportedEncodingException {

        final ByteBuffer yearSexBloodBuffer = ByteBuffer.allocate(2).put(bytes, 0, 2);
        byte sex = (byte) ((yearSexBloodBuffer.get(0) & 0b00011000) >> 3);
        byte bloodType = (byte) ((yearSexBloodBuffer.get(0) & 0b11100000) >> 5);
        yearSexBloodBuffer.rewind();
        short bithdateYear = (short) (yearSexBloodBuffer.getShort() & 0b0000011111111111);

        final EmergencyData emergencyData = new EmergencyData();
        emergencyData.setSex(getSex(sex));
        emergencyData.setBloodType(getBlood(bloodType));
        emergencyData.setAge(LocalDate.now().getYear() - bithdateYear);
        setAllergiesAndPathologies(bytes, emergencyData);

        byte urlLength = bytes[3];
        emergencyData.setUUID(new String(bytes, 4, urlLength, CHARSET_NAME));

        int urlEnd = 3 + urlLength;
        if (bytes.length > urlEnd) {
            byte nameLength = bytes[urlEnd + 1];
            emergencyData.setContactName(new String(bytes, urlEnd + 2, nameLength, CHARSET_NAME));
            int nameEnd = urlEnd + 1 + nameLength;

            byte phoneLength = bytes[nameEnd + 1];
            emergencyData.setContactPhone(new String(bytes, nameEnd + 2, phoneLength, CHARSET_NAME));
        }

        return emergencyData;

    }

    private static void setAllergiesAndPathologies(final byte[] content, final EmergencyData emergencyData) {
        final BitSet bitSet = BitSet.valueOf(content);
        if (bitSet.get(16)) {
            emergencyData.getAllergies().add("Penicilina");
        }
        if (bitSet.get(17)) {
            emergencyData.getAllergies().add("Insulina");
        }
        if (bitSet.get(18)) {
            emergencyData.getAllergies().add("Rayos X con yodo");
        }
        if (bitSet.get(19)) {
            emergencyData.getAllergies().add("Sulfamidas");
        }

        if (bitSet.get(20)) {
            emergencyData.getPathologies().add("Hipertension");
        }
        if (bitSet.get(21)) {
            emergencyData.getPathologies().add("Asma");
        }
        if (bitSet.get(22)) {
            emergencyData.getPathologies().add("Antecedentes Oncologicos");
        }
        if (bitSet.get(23)) {
            emergencyData.getPathologies().add("Insuficiencia Suprarrenal");
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

    private static String getSex(final byte sex) {
        switch (sex) {
            case 0b00:
                return "Masculino";
            case 0b01:
                return "Femenino";
            default:
                return "Otro";
        }
    }


}

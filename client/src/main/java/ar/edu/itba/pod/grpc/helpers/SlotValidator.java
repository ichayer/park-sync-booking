package ar.edu.itba.pod.grpc.helpers;

public class SlotValidator {
    public static boolean isValidSlot(String slot) {
        // Split the slot into hours and minutes
        String[] parts = slot.split(":");
        if (parts.length != 2) {
            return false;
        }

        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            // Validate hours and minutes are in the correct range
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

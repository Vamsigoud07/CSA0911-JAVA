package RedesignedSolution;

/**
 * Q7 Day-4 Design Challenge: Redesigned Solution
 * Architecture: Pre-allocated mutable StringBuilder eliminating re-allocations and copying.
 *
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class OptimizedTicketGenerator {

    private static final String[] FIELD_KEYS = {
        "PNR", "TrainNo", "TrainName", "Source", "Destination",
        "JourneyDate", "ClassType", "Quota", "PassengerName", "Age",
        "Gender", "Coach", "Berth", "BerthType", "BookingStatus",
        "CurrentStatus", "TicketFare", "CateringCharge", "DynamicFare", "GST",
        "TotalFare", "PaymentMode", "TxnId", "IRCTC_Fee", "BankRefNo",
        "BoardingStation", "ReservationUpto", "ScheduledDeparture", "ScheduledArrival", "DistanceKm",
        "TravelTime", "PassengerContact", "IDCardType", "IDCardNo", "FoodChoice",
        "BedrollOpted", "AutoUpgradation", "InsuranceOpted", "InsurancePolicyNo", "EmergencyContact",
        "ConcessionType", "GateNo", "PlatformNo", "TIN", "AgentID",
        "ChartStatus", "QRCodeString", "SecurityHash", "FareRuleCode", "CancellationTerms"
    };

    /**
     * Generates a railway e-ticket using a pre-sized StringBuilder.
     * Complexity: O(F) linear time, 0 intermediate buffer expansions, exactly 2 objects per ticket.
     */
    public String generateTicket(int ticketIndex, int fieldCount) {
        // Pre-size capacity based on average field length (~20 chars * F)
        int estimatedCapacity = Math.max(128, fieldCount * 22);
        StringBuilder sb = new StringBuilder(estimatedCapacity);

        for (int i = 0; i < fieldCount; i++) {
            String key = FIELD_KEYS[i % FIELD_KEYS.length];
            sb.append(key).append(":VAL_").append(ticketIndex).append("_").append(i).append(";");
        }

        return sb.toString();
    }
}

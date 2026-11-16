package OriginalSolution;

/**
 * Q7 Day-4 Design Challenge: Original Solution
 * Demonstrating the naive repeated String concatenation (+) bottleneck.
 *
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class OriginalTicketGenerator {

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
     * Generates a railway e-ticket by repeatedly concatenating strings using '+'.
     * Complexity: O(F^2) time where F is field count, producing (F-1) intermediate strings.
     */
    public String generateTicket(int ticketIndex, int fieldCount) {
        String ticket = "";
        for (int i = 0; i < fieldCount; i++) {
            String key = FIELD_KEYS[i % FIELD_KEYS.length];
            String val = "VAL_" + ticketIndex + "_" + i;
            ticket = ticket + key + ":" + val + ";";
        }
        return ticket;
    }
}

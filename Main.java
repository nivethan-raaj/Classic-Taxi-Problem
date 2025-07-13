import java.util.*;

public class Main {
    class Taxi {
        int id;
        char curloc;
        int availT;
        int total;
        List<String> tripHistory;

        public Taxi(int id) {
            this.id = id;
            this.curloc = 'A';
            this.availT = 5;
            this.total = 0;
            this.tripHistory = new ArrayList<>();
        }

        /*
         * @Override
         * public String toString() {
         * return
         * String.format("Taxi %d: Location=%c, Available at=%d:00, Earnings=Rs.%d",
         * id, curloc, availT, total);
         * }
         */
    }

    class Booking {
        char pickup;
        char destination;
        int bookingTime;
        int assignedTaxiId;
        int fare;

        public Booking(char pickup, char destination, int bookingTime) {
            this.pickup = pickup;
            this.destination = destination;
            this.bookingTime = bookingTime;
        }
    }

    private static final int maxhrs = 20;
    private static final int startime = 5;
    private static final int farepkm = 5;
    private static List<Taxi> taxis = new ArrayList<>();
    private static List<Booking> bookings = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Fare: Rs. 100 basefare +Rs.5perkm");
        Main tm = new Main();
        tm.initializeTaxis();
        while (true) {
            tm.displayMenu();
            int choice = tm.getIntInput("Enter choice: ");
            switch (choice) {
                case 1:
                    tm.bookTaxi();
                    break;
                case 2:
                    tm.showAllDetails();
                    break;
                case 3:
                    System.out.println("Exit");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void initializeTaxis() {
        System.out.print("Enter number of taxi: ");
        int numTaxis = getIntInput("");
        if (numTaxis <= 0 || numTaxis > 2000) {
            System.out.println("Invalid number of taxis Set to 10.");
            numTaxis = 10;
            // case 1
        }
        for (int i = 1; i <= numTaxis; i++) {
            taxis.add(new Taxi(i));
        }
        System.out.println("All taxi are init at point A and avail from 5hrs \n");
    }

    private void displayMenu() {
        System.out.println("1. Book a Taxi");
        System.out.println("2. Earnings Report");
        System.out.println("3. Exit");
    }

    private void bookTaxi() {
        int bookingTime = getIntInput("Enter booking time (bef20hrs): ");
        if (bookingTime < startime || bookingTime >= startime + maxhrs) {
            System.out.println("Invalid booking time!");
            return;
            // case2
        }
        char pickup = getPointInput("Enter pickup point (A,B,C,D<E): ");
        char destination = getPointInput("Enter destination point (A,B,C,D<E): ");

        if (pickup == destination) {
            System.out.println("Pickup and destination cat be the same!");
            return;
            // case 3
        }

        Taxi assignedTaxi = findBestTaxi(pickup, bookingTime);
        if (assignedTaxi == null) {
            System.out.println("No taxi available for this booking!");
            return;
            // case4
        }

        int tripDistance = getDistance(pickup, destination);
        int fare = 100 + (tripDistance * farepkm);
        int tripEndTime = bookingTime + getDistance(pickup, destination);

        assignedTaxi.curloc = destination;
        assignedTaxi.availT = tripEndTime;
        assignedTaxi.total += fare;

        Booking booking = new Booking(pickup, destination, bookingTime);
        booking.assignedTaxiId = assignedTaxi.id;
        booking.fare = fare;
        bookings.add(booking);

        String tripDetails = String.format("Time: %d:00, %c→%c, Fare: Rs. %d",
                bookingTime, pickup, destination, fare);
        assignedTaxi.tripHistory.add(tripDetails);   
    }

    private Taxi findBestTaxi(char pickup, int bookingTime) {
        List<Taxi> availableTaxis = new ArrayList<>();
        for (Taxi taxi : taxis) {
            int timeToReachPickup = getDistance(taxi.curloc, pickup);
            int requiredTime = taxi.availT + timeToReachPickup;
            if (requiredTime <= bookingTime) {
                availableTaxis.add(taxi);
            }
        }
        if (availableTaxis.isEmpty()) {
            return null;
        }
        availableTaxis.sort((t1, t2) -> {
            if (t1.total != t2.total) {
                return Integer.compare(t1.total, t2.total);
            }
            int dist1 = getDistance(t1.curloc, pickup);
            int dist2 = getDistance(t2.curloc, pickup);
            return Integer.compare(dist1, dist2);
        });

        return availableTaxis.get(0);
    }

    private int getDistance(char from, char to) {
        return Math.abs(from - to);
    }

    private void showAllDetails() {
        List<Taxi> sortedTaxis = new ArrayList<>(taxis);
        sortedTaxis.sort((t1, t2) -> Integer.compare(t2.total, t1.total));

        int total = 0;
        int activeTaxis = 0;

        System.out.println("Taxi ID  Earnings  Trips  Current Location");
        for (Taxi taxi : sortedTaxis) {
            if (taxi.total > 0) {
                activeTaxis++;
            }
            total += taxi.total;
            System.out.printf("  %4d  | Rs.%6d  |  %3d  |      %c%n",
                    taxi.id, taxi.total, taxi.tripHistory.size(), taxi.curloc);
        }
        System.out.println("Total Revenue: Rs." + total);
        System.out.println("Active Taxis: " + activeTaxis + "/" + taxis.size());
    }

    private char getPointInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.length() == 1 && input.charAt(0) >= 'A' && input.charAt(0) <= 'E') {
                return input.charAt(0);
            }
            System.out.println("Invalid enter ABCDE.");
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                if (!prompt.isEmpty()) {
                    System.out.print(prompt);
                }
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid inp.");
            }
        }
    }
}
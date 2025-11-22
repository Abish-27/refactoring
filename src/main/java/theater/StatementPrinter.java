package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {

    /** Base amount charged for a tragedy performance (in cents). */
    private static final int TRAGEDY_BASE_AMOUNT = 40000;

    /** Extra amount per audience member above the threshold for tragedies (in cents). */
    private static final int TRAGEDY_EXTRA_AMOUNT_PER_AUDIENCE = 1000;

    /** Number of cents in one dollar. */
    private static final int CENTS_IN_DOLLAR = 100;

    private final Invoice invoice;
    private final Map<String, Play> plays;

    /**
     * Creates a new StatementPrinter for the given invoice and plays.
     *
     * @param invoice the invoice to generate statements for
     * @param plays   the mapping from play IDs to Play objects
     */
    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns the invoice associated with this statement printer.
     *
     * @return the invoice for this printer
     */
    public Invoice getInvoice() {
        return invoice;
    }

    /**
     * Returns the map from play IDs to plays used by this statement printer.
     *
     * @return the plays map
     */
    public Map<String, Play> getPlays() {
        return plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     *
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        final StringBuilder result =
                new StringBuilder("Statement for " + getInvoice().getCustomer() + System.lineSeparator());

        for (Performance performance : getInvoice().getPerformances()) {
            result.append(String.format(
                    "  %s: %s (%s seats)%n",
                    getPlay(performance).getName(),
                    usd(getAmount(performance)),
                    performance.getAudience()));
        }
        result.append(String.format("Amount owed is %s%n",
                usd(getTotalAmount())));
        result.append(String.format("You earned %s credits%n", getTotalVolumeCredits()));
        return result.toString();
    }

    private int getTotalAmount() {
        int result = 0;
        for (Performance performance : getInvoice().getPerformances()) {
            result += getAmount(performance);
        }
        return result;
    }

    private int getTotalVolumeCredits() {
        int result = 0;
        for (Performance performance : getInvoice().getPerformances()) {
            // add volume credits
            result += getVolumeCredits(performance);
        }
        return result;
    }

    private static String usd(int totalAmount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(totalAmount / CENTS_IN_DOLLAR);
    }

    private int getVolumeCredits(Performance performance) {
        int result = 0;
        result += Math.max(performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        // add extra credit for every five comedy attendees
        if ("comedy".equals(getPlay(performance).getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    private Play getPlay(Performance performance) {
        return getPlays().get(performance.getPlayID());
    }

    private int getAmount(Performance performance) {
        int result = 0;
        switch (getPlay(performance).getType()) {
            case "tragedy":
                result = TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += TRAGEDY_EXTRA_AMOUNT_PER_AUDIENCE
                            * (performance.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;
            default:
                throw new RuntimeException(String.format("unknown type: %s", getPlay(performance).getType()));
        }
        return result;
    }
}

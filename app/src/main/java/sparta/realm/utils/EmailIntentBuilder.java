package sparta.realm.utils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Patterns;




/**
 * A helper to create email intents, i.e. {@link Intent#ACTION_SENDTO} with a {@code mailto:} URI.
 *
 * <p>Example usage:</p>
 * <pre>
 * <code>
 * EmailIntentBuilder.from(activity)
 *         .to("alice@example.org")
 *         .subject("Bug report for 'My awesome app'")
 *         .body("Something went wrong :(")
 *         .start();
 * </code>
 * </pre>
 * <p>This creates an intent containing the following {@code mailto:} URI:</p>
 * <pre>
 * <code>
 * mailto:alice@example.org?subject=Bug%20report%20for%20'My%20awesome%20app'&amp;body=Something%20went%20wrong%20%3A(
 * </code>
 * </pre>
 *
 * @see #from(Context)
 */
@SuppressWarnings("WeakerAccess")
public final class EmailIntentBuilder {
    private final Context context;
    private final Set<String> to = new LinkedHashSet<>();
    private final Set<String> cc = new LinkedHashSet<>();
    private final Set<String> bcc = new LinkedHashSet<>();
    private String subject;
    private String body;


    private EmailIntentBuilder( Context context) {
        this.context = checkNotNull(context);
    }

    /**
     * Create a builder to create an {@link Intent#ACTION_SENDTO} intent or to launch that intent.
     *
     * @param context
     *         The {@code Context} that will be used to launch the intent
     *
     * @return An email intent builder
     */
   
    public static EmailIntentBuilder from( Context context) {
        return new EmailIntentBuilder(context);
    }

    /**
     * Add an email address to be used in the "to" field.
     *
     * @param to
     *         the email address to add
     *
     * @return This {@code EmailIntentBuilder} for method chaining
     */
   
    public EmailIntentBuilder to( String to) {
        checkEmail(to);
        this.to.add(to);
        return this;
    }

    /**
     * Add a list of email addresses to be used in the "to" field.
     *
     * @param to
     *         the email addresses to add
     *
     * @return This {@code EmailIntentBuilder} for method chaining
     */
   
    public EmailIntentBuilder to( Collection<String> to) {
        checkNotNull(to);
        for (String email : to) {
            checkEmail(email);
        }
        this.to.addAll(to);

        return this;
    }

    /**
     * Add an email address to be used in the "cc" field.
     *
     * @param cc
     *         the email address to add
     *
     * @return This {@code EmailIntentBuilder} for method chaining
     */
   
    public EmailIntentBuilder cc( String cc) {
        checkEmail(cc);
        this.cc.add(cc);
        return this;
    }

    /**
     * Add an email address to be used in the "cc" field.
     *
     * @param cc
     *         the email addresses to add
     *
     * @return This {@code EmailIntentBuilder} for method chaining
     */
   
    public EmailIntentBuilder cc( Collection<String> cc) {
        checkNotNull(cc);
        for (String email : cc) {
            checkEmail(email);
        }
        this.cc.addAll(cc);

        return this;
    }

    /**
     * Add an email address to be used in the "bcc" field.
     *
     * @param bcc
     *         the email address to add
     *
     * @return This {@code EmailIntentBuilder} for method chaining
     */
   
    public EmailIntentBuilder bcc( String bcc) {
        checkEmail(bcc);
        this.bcc.add(bcc);
        return this;
    }

    /**
     * Add an email address to be used in the "bcc" field.
     *
     * @param bcc
     *         the email addresses to add
     *
     * @return This {@code EmailIntentBuilder} for method chaining
     */
   
    public EmailIntentBuilder bcc( Collection<String> bcc) {
        checkNotNull(bcc);
        for (String email : bcc) {
            checkEmail(email);
        }
        this.bcc.addAll(bcc);

        return this;
    }

    /**
     * Set the subject line for this email intent.
     *
     * @param subject
     *         the email subject line
     *
     * @return This {@code EmailIntentBuilder} for method chaining
     */
   
    public EmailIntentBuilder subject( String subject) {
        checkNotNull(subject);
        checkNoLineBreaks(subject);
        this.subject = subject;
        return this;
    }

    /**
     * Set the text body for this email intent.
     *
     * @param body
     *         the text body
     *
     * @return This {@code EmailIntentBuilder} for method chaining
     */
   
    public EmailIntentBuilder body( String body) {
        checkNotNull(body);
        this.body = fixLineBreaks(body);
        return this;
    }

    /**
     * Launch the email intent.
     *
     * @return {@code false} if no activity to handle the email intent could be found; {@code true} otherwise
     */
    public boolean start() {
        Intent emailIntent = build();
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            return false;
        }

        return true;
    }

    private void startActivity(Intent intent) {
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    /**
     * Build the {@link Intent#ACTION_SENDTO} intent.
     *
     * @return the intent containing the provided information
     */
   
    public Intent build() {
        Uri mailtoUri = constructMailtoUri();
        return new Intent(Intent.ACTION_SENDTO, mailtoUri);
    }

   
    private Uri constructMailtoUri() {
        StringBuilder mailto = new StringBuilder(1024);
        mailto.append("mailto:");
        addRecipients(mailto, to);

        boolean hasQueryParameters;
        hasQueryParameters = addRecipientQueryParameters(mailto, "cc", cc, false);
        hasQueryParameters = addRecipientQueryParameters(mailto, "bcc", bcc, hasQueryParameters);
        hasQueryParameters = addQueryParameter(mailto, "subject", subject, hasQueryParameters);
        addQueryParameter(mailto, "body", body, hasQueryParameters);

        return Uri.parse(mailto.toString());
    }

    private boolean addQueryParameter(StringBuilder mailto, String field, String value, boolean hasQueryParameters) {
        if (value == null) {
            return hasQueryParameters;
        }

        mailto.append(hasQueryParameters ? '&' : '?').append(field).append('=').append(Uri.encode(value));

        return true;
    }

    private boolean addRecipientQueryParameters(StringBuilder mailto, String field, Set<String> recipients,
                                                boolean hasQueryParameters) {
        if (recipients.isEmpty()) {
            return hasQueryParameters;
        }

        mailto.append(hasQueryParameters ? '&' : '?').append(field).append('=');
        addRecipients(mailto, recipients);

        return true;
    }

    private void addRecipients(StringBuilder mailto, Set<String> recipients) {
        if (recipients.isEmpty()) {
            return;
        }

        for (String recipient : recipients) {
            mailto.append(encodeRecipient(recipient));
            mailto.append(',');
        }

        mailto.setLength(mailto.length() - 1);
    }

    private void checkEmail(String email) {
        checkNotNull(email);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw new IllegalArgumentException("Argument is not a valid email address (according to " +
                    "Patterns.EMAIL_ADDRESS)");
        }
    }

    private void checkNoLineBreaks(String text) {
        boolean containsCarriageReturn = text.indexOf('\r') != -1;
        boolean containsLineFeed = text.indexOf('\n') != -1;

        if (containsCarriageReturn || containsLineFeed) {
            throw new IllegalArgumentException("Argument must not contain line breaks");
        }
    }

   
    private static <T> T checkNotNull(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Argument must not be null");
        }

        return object;
    }

   
    static String encodeRecipient(String recipient) {
        int index = recipient.lastIndexOf('@');
        String localPart = recipient.substring(0, index);
        String host = recipient.substring(index + 1);
        return Uri.encode(localPart) + "@" + Uri.encode(host);
    }

   
    static String fixLineBreaks(String text) {
        return text.replaceAll("\r\n", "\n").replace('\r', '\n').replaceAll("\n", "\r\n");
    }
}
package org.project.notificationservice.entity;

import lombok.RequiredArgsConstructor;
import org.project.notificationservice.repository.NotificationTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemplateSeeder implements CommandLineRunner {

    private final NotificationTemplateRepository repo;

    @Override
    public void run(String... args) {

        // --- 1. ESTIMATE SENT (Email & SMS) ---
        seed("estimate-sent-email",
                "Your Estimate from OlivesGreen is Ready",
                """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">
                    <div style="max-width: 600px; margin: 0 auto; border: 1px solid #eee; padding: 20px; border-radius: 8px;">
                        <h2 style="color: #059669;">Hello {{customerName}},</h2>
                        <p>Your estimate for <strong>{{quoteTitle}}</strong> is ready for review.</p>
                        <p>Click the button below to view the details and approve the work:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="{{magicLink}}" style="background-color: #059669; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;">View Estimate</a>
                        </div>
                        <p style="font-size: 12px; color: #777;">If the button doesn't work, copy this link: <br/> {{magicLink}}</p>
                        <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                        <p style="font-size: 12px; text-align: center; color: #999;">&copy; {{year}} OlivesGreen Services</p>
                    </div>
                </body>
                </html>
                """,
                null);

        seed("estimate-sent-sms",
                null,
                null,
                "Hi {{customerName}}, your estimate for \"{{quoteTitle}}\" is ready. Review it here: {{magicLink}} - OlivesGreen");


        // --- 2. DEPOSIT RECEIPT (Email) ---
        seed("deposit-receipt",
                "Payment Received: Deposit for {{quoteTitle}}",
                """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e5e7eb; border-radius: 8px;">
                        <h2 style="color: #059669;">Payment Received</h2>
                        <p>Hi {{customerName}},</p>
                        <p>Thank you! We have received your deposit. Your job is now confirmed.</p>
                        
                        <div style="background: #f9fafb; padding: 15px; border-radius: 8px; margin: 20px 0;">
                            <p style="margin: 5px 0;"><strong>Amount Paid:</strong> ${{amountPaid}}</p>
                            <p style="margin: 5px 0;"><strong>Date:</strong> {{date}}</p>
                            <p style="margin: 5px 0; font-size: 12px; color: #6b7280;">Transaction ID: {{transactionId}}</p>
                        </div>

                        <p>We will contact you shortly to schedule the service.</p>
                    </div>
                </body>
                </html>
                """,
                null);


        // --- 3. JOB ASSIGNED (Notification to Employee) ---
        seed("job-assigned",
                "New Job Assigned: {{jobTitle}}",
                """
                <html>
                <body>
                    <h3>Hello {{employeeName}},</h3>
                    <p>You have been assigned to a new job: <strong>{{jobTitle}}</strong>.</p>
                    <p><strong>Scheduled Date:</strong> {{date}}</p>
                    <p>Please check your dashboard for details.</p>
                </body>
                </html>
                """,
                "New Job Assigned: {{jobTitle}} on {{date}}. Check app for details.");


        // --- 4. FINAL INVOICE READY (Email & SMS) ---
        seed("final-invoice-ready",
                "Invoice #{{invoiceId}} from OlivesGreen",
                """
                <html>
                <body style="font-family: sans-serif; color: #333;">
                    <div style="padding: 20px; border: 1px solid #ddd; border-radius: 8px; max-width: 600px; margin: auto;">
                        <h2 style="color: #1f2937;">Final Invoice Ready</h2>
                        <p>Hi {{customerName}},</p>
                        <p>The work for your project is complete. The final invoice is now available.</p>
                        
                        <div style="margin: 20px 0; padding: 15px; background: #f3f4f6; border-radius: 6px;">
                            <p style="font-size: 18px; font-weight: bold; color: #059669;">Balance Due: ${{amount}}</p>
                            <p>Due Date: {{dueDate}}</p>
                        </div>

                        <div style="text-align: center; margin-top: 30px;">
                            <a href="{{link}}" style="background: #1f2937; color: #fff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">Pay Invoice</a>
                        </div>
                    </div>
                </body>
                </html>
                """,
                null);

        seed("final-invoice-ready-sms",
                null,
                null,
                "Hi {{customerName}}, your final invoice for ${{amount}} is ready. Pay here: {{link}}");
    }

    private void seed(String key, String emailSubject, String emailBody, String smsBody) {
        if (repo.findByTemplateKey(key).isEmpty()) {
            NotificationTemplate t = NotificationTemplate.builder()
                    .templateKey(key)
                    .emailSubject(emailSubject)
                    .emailBody(emailBody)
                    .smsBody(smsBody)
                    .build();
            repo.save(t);
            System.out.println("Inserted template: " + key);
        }
    }
}
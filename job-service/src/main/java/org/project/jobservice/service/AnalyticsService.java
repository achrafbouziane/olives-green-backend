package org.project.jobservice.service;

import com.stripe.Stripe;
import com.stripe.model.Balance;
import lombok.RequiredArgsConstructor;
import org.project.jobservice.domain.JobStatus;
import org.project.jobservice.domain.QuoteStatus;
import org.project.jobservice.repository.JobRepository;
import org.project.jobservice.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final JobRepository jobRepository;
    private final QuoteRepository quoteRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            Stripe.apiKey = stripeApiKey;
            Balance balance = Balance.retrieve();
            long available = balance.getAvailable().get(0).getAmount();
            long pending = balance.getPending().get(0).getAmount();

            stats.put("stripeAvailable", BigDecimal.valueOf(available).divide(BigDecimal.valueOf(100)));
            stats.put("stripePending", BigDecimal.valueOf(pending).divide(BigDecimal.valueOf(100)));
        } catch (Exception e) {
            stats.put("stripeAvailable", 0);
            stats.put("stripePending", 0);
            // Log error (don't crash dashboard if Stripe fails)
        }

        // 2. JOB METRICS
        stats.put("totalJobs", jobRepository.count());
        stats.put("completedJobs", jobRepository.countByStatus(JobStatus.COMPLETED));
        stats.put("activeJobs", jobRepository.countByStatus(JobStatus.IN_PROGRESS));

        // 3. QUOTE METRICS (Funnel)
        long requested = quoteRepository.countByStatus(QuoteStatus.REQUESTED);
        long approved = quoteRepository.countByStatus(QuoteStatus.APPROVED);
        long paid = quoteRepository.countByStatus(QuoteStatus.DEPOSIT_PAID);

        stats.put("quotesRequested", requested);
        stats.put("quotesConverted", approved + paid);

        // Calculate Conversion Rate
        double conversionRate = requested == 0 ? 0 : ((double)(approved + paid) / (requested + approved + paid)) * 100;
        stats.put("conversionRate", Math.round(conversionRate * 10.0) / 10.0);

        return stats;
    }
}
package org.project.jobservice.mapper;

import org.mapstruct.Mapping;
import org.project.jobservice.dto.JobDTO;
import org.project.jobservice.dto.LineItemDTO;
import org.project.jobservice.dto.QuoteDTO;
import org.project.jobservice.entity.Job;
import org.project.jobservice.entity.LineItem;
import org.project.jobservice.entity.Quote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobMapper {

    QuoteDTO mapToQuoteDTO(Quote quote);

    LineItemDTO mapToLineItemDTO(LineItem lineItem);

    @Mapping(target = "quoteId", source = "quote.id")
    JobDTO mapToJobDTO(Job job);
}
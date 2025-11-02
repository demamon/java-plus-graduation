package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventSimilarityMapper {

    EventSimilarity mapToEventSimilarity(EventSimilarityAvro similarityAvro);

}

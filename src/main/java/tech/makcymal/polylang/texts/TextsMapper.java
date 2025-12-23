package tech.makcymal.polylang.texts;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TextsMapper {

    TextDto toDto(TextEntity text);

}

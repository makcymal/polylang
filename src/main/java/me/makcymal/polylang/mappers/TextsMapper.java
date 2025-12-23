package me.makcymal.polylang.mappers;

import me.makcymal.polylang.dtos.TextDto;
import me.makcymal.polylang.entities.Text;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TextsMapper {

    TextDto toDto(Text text);

}

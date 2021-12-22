package land.metadefi.mapper;

import land.metadefi.entity.ContractEventEntity;
import land.metadefi.model.ContractEventDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;

@Mapper( imports = { Timestamp.class } )
public interface ContractEventMapper {
    ContractEventMapper INSTANCE = Mappers.getMapper(ContractEventMapper.class);

    @Mapping(source = "id", target = "eventId")
    @Mapping(target = "id", ignore = true)
    ContractEventEntity toEntity(ContractEventDetail event);
}

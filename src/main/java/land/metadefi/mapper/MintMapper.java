package land.metadefi.mapper;

import land.metadefi.entity.MintHistoryEntity;
import land.metadefi.model.NFT;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MintMapper {
    MintMapper INSTANCE = Mappers.getMapper(MintMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    MintHistoryEntity toEntity(NFT mintToken);
}

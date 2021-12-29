package land.metadefi.mapper;

import land.metadefi.entity.MintHistoryEntity;
import land.metadefi.model.MintNFT;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MintMapper {
    MintMapper INSTANCE = Mappers.getMapper(MintMapper.class);

    @Mapping(target = "id", ignore = true)
    MintHistoryEntity toEntity(MintNFT mintToken);
}

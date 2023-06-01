package hse.sachkov.learningtrackbackend.api.material;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMaterialLike {

    Long materialId;
    String username;
    Long userExternalId;
    List<Long> likedMaterialsList;

    public UserMaterialLike(Long materialId, String username, Long userExternalId) {
        this.materialId = materialId;
        this.username = username;
        this.userExternalId = userExternalId;
    }
}

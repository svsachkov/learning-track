package hse.sachkov.learningtrackbackend.api.material;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends CrudRepository<Material, Long> {

    @Modifying
    @Transactional
    @Query(value = "insert into user_material_like (material_id, username) values (:material_id, :username)", nativeQuery = true)
    void addLike(@Param("material_id") Long materialId, @Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "delete from user_material_like where material_id = :material_id and username = :username", nativeQuery = true)
    void removeLike(@Param("material_id") Long materialId, @Param("username") String username);

    @Query(value = "select new hse.sachkov.learningtrackbackend.api.material.UserMaterialLike(m.id, lu.username, lu.externalId) from Material m JOIN m.likedUsers lu WHERE lu.username in :usernames")
    List<UserMaterialLike> getAllLikesWhereUsernameIn(List<String> usernames);
}

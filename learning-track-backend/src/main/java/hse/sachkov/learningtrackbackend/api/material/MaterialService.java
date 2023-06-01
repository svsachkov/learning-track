package hse.sachkov.learningtrackbackend.api.material;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public void like(Long materialId, String username) {
        materialRepository.addLike(materialId, username);
    }

    public void removeLike(Long materialId, String username) {
        materialRepository.removeLike(materialId, username);
    }
}

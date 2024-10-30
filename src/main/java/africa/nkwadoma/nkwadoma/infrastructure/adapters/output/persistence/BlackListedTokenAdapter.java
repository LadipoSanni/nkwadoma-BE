package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.BlackListedToken;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.BlackListedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BlackListedTokenAdapter {

    private final BlackListedTokenRepository blackListedTokenRepository;

    public List<BlackListedToken> findAll() {
        return blackListedTokenRepository.findAll();
    }

    public BlackListedToken saveBlackListedToken(BlackListedToken accessToken){
        return blackListedTokenRepository.save(accessToken);
    }

    public boolean isPresent(String accessToken) {
        return blackListedTokenRepository.existsById(accessToken);
    }

    public void deleteToken(BlackListedToken blackListedToken) {
        blackListedTokenRepository.delete(blackListedToken);
    }
}

package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.BlackListedToken;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.BlackListedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlackListedTokenAdapter {

    private final BlackListedTokenRepository blackListedTokenRepository;

    public  List<BlackListedToken> findExpiredTokens(){
        return blackListedTokenRepository.findExpiredTokens();
    }

    public void blackListToken(BlackListedToken accessToken){
        log.info("Token blacklisted!");
        blackListedTokenRepository.save(accessToken);
    }

    public boolean isPresent(String accessToken) {
        return blackListedTokenRepository.existsById(accessToken);
    }

    public void deleteToken(BlackListedToken blackListedToken) {
        blackListedTokenRepository.delete(blackListedToken);
    }
}

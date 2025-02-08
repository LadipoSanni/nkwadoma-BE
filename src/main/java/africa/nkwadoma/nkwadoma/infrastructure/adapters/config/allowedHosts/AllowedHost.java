package africa.nkwadoma.nkwadoma.infrastructure.adapters.config.allowedHosts;


public interface AllowedHost {
    String[] methods = {
            "GET",
            "POST",
            "PATCH",
            "DELETE",
            "OPTIONS",
            "HEAD"
    };
    String[] getPatterns();

    default String[] getMethods(){
        return methods;
    }

}

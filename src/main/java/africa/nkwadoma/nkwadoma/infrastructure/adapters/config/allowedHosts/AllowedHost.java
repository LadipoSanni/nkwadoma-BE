package africa.nkwadoma.nkwadoma.infrastructure.adapters.config.allowedHosts;


public interface AllowedHost {
    String[] methods = {
            "GET",
            "POST",
            "PATCH",
            "DELETE",
            "OPTIONS",
            "HEAD"
            "http://172.16.1.52:3000"
    };
    String[] getPatterns();

    default String[] getMethods(){
        return methods;
    }

}

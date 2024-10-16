package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;


public interface AllowedHost {
    String[] methods = {
            "GET",
            "POST",
            "DELETE",
            "OPTIONS",
            "HEAD"
    };
    String[] getPatterns();

    default String[] getMethods(){
        return methods;
    }

}

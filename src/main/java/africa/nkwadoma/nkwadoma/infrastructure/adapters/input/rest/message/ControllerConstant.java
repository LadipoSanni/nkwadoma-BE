package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message;

import lombok.Getter;

@Getter
public class ControllerConstant {
   public static final String  INVITE_ORGANIZATION_TITLE = "Invite Organization";
   public static final String  INVITE_ORGANIZATION_DESCRIPTION = "To invite an organization, kindly provide the industry name,organization name, email address, website address,rc number,tin and phone number ";
   public static final String  LOAN_PRODUCT_CREATION = "create loan product";
   public static final String  LOAN_PRODUCT_UPDATE = "update loan product";
   public static final String  VIEW_LOAN_PRODUCT_DETAILS = "View loan product details by id.";
   public static final String  VIEW_LOAN_PRODUCT_DETAILS_DESCRIPTION = "This endpoint is used to view the details of a loan product with unique id";
   public static final String  LOAN_PRODUCT_CREATION_DESCRIPTION = "To create a loan product with unique name";
   public static final String  LOAN_PRODUCT_UPDATE_DESCRIPTION = "To update a loan product with unique name. The id of the loan product must be provided";


}

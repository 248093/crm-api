package com.crm.convert;

import com.crm.entity.Customer;
import com.crm.vo.CustomerVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-26T20:23:42+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
public class CustomerConvertImpl implements CustomerConvert {

    @Override
    public Customer convert(CustomerVO customerVO) {
        if ( customerVO == null ) {
            return null;
        }

        Customer customer = new Customer();

        customer.setId( customerVO.getId() );
        customer.setName( customerVO.getName() );
        customer.setPhone( customerVO.getPhone() );
        customer.setEmail( customerVO.getEmail() );
        customer.setLevel( customerVO.getLevel() );
        customer.setSource( customerVO.getSource() );
        customer.setAddress( customerVO.getAddress() );
        customer.setFollowStatus( customerVO.getFollowStatus() );
        customer.setNextFollowStatus( customerVO.getNextFollowStatus() );
        customer.setRemark( customerVO.getRemark() );
        customer.setCreaterId( customerVO.getCreaterId() );
        customer.setIsPublic( customerVO.getIsPublic() );
        customer.setOwnerId( customerVO.getOwnerId() );
        customer.setIsKeyDecisionMaker( customerVO.getIsKeyDecisionMaker() );
        customer.setGender( customerVO.getGender() );
        customer.setDealCount( customerVO.getDealCount() );
        customer.setCreateTime( customerVO.getCreateTime() );

        return customer;
    }

    @Override
    public CustomerVO convert(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerVO customerVO = new CustomerVO();

        customerVO.setId( customer.getId() );
        customerVO.setName( customer.getName() );
        customerVO.setPhone( customer.getPhone() );
        customerVO.setEmail( customer.getEmail() );
        customerVO.setLevel( customer.getLevel() );
        customerVO.setSource( customer.getSource() );
        customerVO.setAddress( customer.getAddress() );
        customerVO.setFollowStatus( customer.getFollowStatus() );
        customerVO.setNextFollowStatus( customer.getNextFollowStatus() );
        customerVO.setRemark( customer.getRemark() );
        customerVO.setCreaterId( customer.getCreaterId() );
        customerVO.setIsPublic( customer.getIsPublic() );
        customerVO.setOwnerId( customer.getOwnerId() );
        customerVO.setIsKeyDecisionMaker( customer.getIsKeyDecisionMaker() );
        customerVO.setGender( customer.getGender() );
        customerVO.setDealCount( customer.getDealCount() );
        customerVO.setCreateTime( customer.getCreateTime() );

        return customerVO;
    }

    @Override
    public List<Customer> convert(List<CustomerVO> customerVOs) {
        if ( customerVOs == null ) {
            return null;
        }

        List<Customer> list = new ArrayList<Customer>( customerVOs.size() );
        for ( CustomerVO customerVO : customerVOs ) {
            list.add( convert( customerVO ) );
        }

        return list;
    }
}

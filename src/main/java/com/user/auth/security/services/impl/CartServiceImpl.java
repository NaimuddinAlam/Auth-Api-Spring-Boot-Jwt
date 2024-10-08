package com.user.auth.security.services.impl;

import com.user.auth.exceptions.APIException;
import com.user.auth.exceptions.ResourceNotFoundException;
import com.user.auth.model.Cart;
import com.user.auth.model.CartItem;
import com.user.auth.model.Product;
import com.user.auth.payload.CartDTO;
import com.user.auth.payload.ProductDTO;
import com.user.auth.repositorys.CartItemRepository;
import com.user.auth.repositorys.CartRepository;
import com.user.auth.repositorys.ProductRepository;
import com.user.auth.security.services.CartService;
import com.user.auth.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private   ProductRepository productRepository;
     @Autowired
    ModelMapper modelMapper;
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart  = createCart();
        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("pproduc","ProductId",productId));
        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);
        if(cartItem!=null)
        {
            throw  new APIException("Product"+product.getProductName()+"already exists in the cart");
        }
        if(product.getQuantity()==0)
        {
            throw  new APIException(product.getProductName()+"is not available");
        }
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem newCartItem= new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        cartItemRepository.save(newCartItem);
        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;





    }

    @Override
    public List<CartDTO> getAllCarts() {
        return List.of();
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        return null;
    }

    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        return null;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        return "";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {

    }

    public Cart createCart()
    {
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null)
        {
            return  userCart;
        }

        Cart cart=new Cart();
         cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart=cartRepository.save(cart);
return  newCart;

    }
}

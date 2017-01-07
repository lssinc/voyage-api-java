package launchpad.security.client

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.EqualsAndHashCode
import launchpad.security.role.Role

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity
@EqualsAndHashCode
class Client {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id

    /**
     * The client name
     */
    @NotNull
    String name

    /**
     * The client equivalent of the user "username". The identifier given when the client is identifying itself to the
     * app. This field is referred to as the "client_id" in OAuth2 specs. This object uses "client_identifier" so that
     * it is not confused with the foreign key column naming pattern "client_id" within the database.
     */
    @NotNull
    String clientIdentifier

    /**
     * The client password. This should be kept secret and only used in a server-to-server authentication process.
     */
    @NotNull
    String clientSecret

    /**
     * Flag to enforce that the client secret is passed in during authentication. This should only be true for clients
     * that will be doing server-to-server communication.
     */
    @NotNull
    Boolean isSecretRequired = Boolean.FALSE

    /**
     * Flag to limit the client to a specific scope. If false, the scope of the authentication request will be ignored.
     */
    @NotNull
    Boolean isScoped = Boolean.FALSE

    /**
     * Flag to skip the user approval page where the client "scopes" are displayed with an "Approve" or "Deny" option.
     * isAutoApprove = true will skip the user approval page
     * isAutoApprove = false will force the user approval page to be displayed
     */
    @NotNull
    Boolean isAutoApprove = Boolean.TRUE

    /**
     * Flag to enable or disable the client
     */
    @NotNull
    Boolean isEnabled = Boolean.TRUE

    /**
     * Flag to logically delete the client
     */
    @NotNull
    Boolean isDeleted = Boolean.FALSE

    /**
     * The number of seconds before the access token for the client is expired.
     */
    @NotNull
    Integer accessTokenValiditySeconds = 7200 // 2 Hours

    /**
     * The number of seconds before the refresh token issued to the client is expired. Refresh tokens should be much
     * longer than the access tokens.
     */
    @NotNull
    Integer refreshTokenValiditySeconds = 86400 // 24 hours

    /**
     * The authentication "grant" types that this client is allowed to request.
     *
     * Grant types include: client_credentials, authorization_code, password, implicit
     *
     * See the OAuth2 specs for more information on these types at http://oauth.com
     */
    @OneToMany(fetch=FetchType.EAGER, mappedBy="client")
    Set<ClientGrant> clientGrants

    /**
     * Scopes that the client is allowed to perform.
     *
     * Scope examples could be: Read Your Contacts, Scan Your Inbox, Send Email To Your Mother
     */
    @OneToMany(fetch=FetchType.EAGER, mappedBy="client")
    Set<ClientScope> clientScopes

    /**
     * A list of approved redirect URIs for this client. When passing sensitive information back to a client (ie access
     * token), the data is appended to the redirect URI and the response is redirected to the redirect URI.
     *
     * Multiple redirect URIs are supported because the client is required to provide a redirect URI in requests that
     * require a redirect. If the given redirect URI exactly matches a URI in the database for the client, then the
     * given URI will be used for the redirect process.
     */
    @OneToMany(fetch=FetchType.EAGER, mappedBy="client")
    Set<ClientRedirectUri> clientRedirectUris

    /**
     * The roles that the client has been granted for accessing resources within the app
     */
    @ManyToMany
    @JoinTable(name='client_role', joinColumns=@JoinColumn(name='client_id'), inverseJoinColumns=@JoinColumn(name='role_id'))
    @JsonIgnore
    Set<Role> roles
}
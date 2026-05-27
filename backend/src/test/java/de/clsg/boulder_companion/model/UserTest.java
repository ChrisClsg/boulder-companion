package de.clsg.boulder_companion.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the User record's immutable mutation methods.
 * Verifies correct handling of add/remove operations, null/blank guards,
 * and duplicate-prevention logic.
 */
class UserTest {

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    private User baseUser() {
        return new User(
                "user-1",
                "github-123",
                "Alice",
                "alice@example.com",
                User.Role.CLIMBER,
                List.of(),
                List.of(),
                List.of(),
                NOW
        );
    }

    // -------------------------------------------------------------------------
    // withRole
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("withRole()")
    class WithRole {

        @Test
        @DisplayName("returns new User with updated role, all other fields unchanged")
        void withRole_newRole_returnsUserWithUpdatedRole() {
            User user = baseUser().withRole(User.Role.GYM_ADMIN);

            assertThat(user.role()).isEqualTo(User.Role.GYM_ADMIN);
            assertThat(user.id()).isEqualTo("user-1");
            assertThat(user.name()).isEqualTo("Alice");
        }

        @Test
        @DisplayName("preserves existing lists when role changes")
        void withRole_preservesExistingLists() {
            User withGyms = baseUser().addFavoriteGym("gym-1");
            User updated = withGyms.withRole(User.Role.ROUTE_SETTER);

            assertThat(updated.role()).isEqualTo(User.Role.ROUTE_SETTER);
            assertThat(updated.favoriteGyms()).containsExactly("gym-1");
        }
    }

    // -------------------------------------------------------------------------
    // addFavoriteGym / removeFavoriteGym
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("addFavoriteGym()")
    class AddFavoriteGym {

        @Test
        @DisplayName("adds a new gymId to an empty list")
        void addFavoriteGym_emptyList_addsGym() {
            User user = baseUser().addFavoriteGym("gym-1");

            assertThat(user.favoriteGyms()).containsExactly("gym-1");
        }

        @Test
        @DisplayName("adds a second distinct gymId")
        void addFavoriteGym_existingEntry_appendsNewGym() {
            User user = baseUser().addFavoriteGym("gym-1").addFavoriteGym("gym-2");

            assertThat(user.favoriteGyms()).containsExactly("gym-1", "gym-2");
        }

        @Test
        @DisplayName("null gymId is a no-op")
        void addFavoriteGym_nullGymId_returnsUnchanged() {
            User original = baseUser();
            User result = original.addFavoriteGym(null);

            assertThat(result).isSameAs(original);
        }

        @Test
        @DisplayName("blank gymId is a no-op")
        void addFavoriteGym_blankGymId_returnsUnchanged() {
            User original = baseUser();
            User result = original.addFavoriteGym("   ");

            assertThat(result).isSameAs(original);
        }

        @Test
        @DisplayName("duplicate gymId is a no-op")
        void addFavoriteGym_duplicateGymId_returnsUnchanged() {
            User withGym = baseUser().addFavoriteGym("gym-1");
            User result = withGym.addFavoriteGym("gym-1");

            assertThat(result).isSameAs(withGym);
            assertThat(result.favoriteGyms()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("removeFavoriteGym()")
    class RemoveFavoriteGym {

        @Test
        @DisplayName("removes an existing gymId")
        void removeFavoriteGym_existingGym_removesIt() {
            User user = baseUser().addFavoriteGym("gym-1").removeFavoriteGym("gym-1");

            assertThat(user.favoriteGyms()).isEmpty();
        }

        @Test
        @DisplayName("removes only the targeted gymId, leaving others intact")
        void removeFavoriteGym_multipleGyms_removesOnlyTarget() {
            User user = baseUser()
                    .addFavoriteGym("gym-1")
                    .addFavoriteGym("gym-2")
                    .removeFavoriteGym("gym-1");

            assertThat(user.favoriteGyms()).containsExactly("gym-2");
        }

        @Test
        @DisplayName("null gymId is a no-op")
        void removeFavoriteGym_nullGymId_returnsUnchanged() {
            User withGym = baseUser().addFavoriteGym("gym-1");
            User result = withGym.removeFavoriteGym(null);

            assertThat(result).isSameAs(withGym);
        }

        @Test
        @DisplayName("blank gymId is a no-op")
        void removeFavoriteGym_blankGymId_returnsUnchanged() {
            User withGym = baseUser().addFavoriteGym("gym-1");
            User result = withGym.removeFavoriteGym("  ");

            assertThat(result).isSameAs(withGym);
        }

        @Test
        @DisplayName("gymId not in list is a no-op")
        void removeFavoriteGym_gymIdNotPresent_returnsUnchanged() {
            User withGym = baseUser().addFavoriteGym("gym-1");
            User result = withGym.removeFavoriteGym("gym-99");

            assertThat(result).isSameAs(withGym);
        }
    }

    // -------------------------------------------------------------------------
    // addGymAdminFor / removeGymAdminFor
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("addGymAdminFor()")
    class AddGymAdminFor {

        @Test
        @DisplayName("adds a new gymId to the gymAdminFor list")
        void addGymAdminFor_emptyList_addsGym() {
            User user = baseUser().addGymAdminFor("gym-1");

            assertThat(user.gymAdminFor()).containsExactly("gym-1");
        }

        @Test
        @DisplayName("null gymId is a no-op")
        void addGymAdminFor_nullGymId_returnsUnchanged() {
            User original = baseUser();
            User result = original.addGymAdminFor(null);

            assertThat(result).isSameAs(original);
        }

        @Test
        @DisplayName("blank gymId is a no-op")
        void addGymAdminFor_blankGymId_returnsUnchanged() {
            User original = baseUser();
            User result = original.addGymAdminFor("");

            assertThat(result).isSameAs(original);
        }

        @Test
        @DisplayName("duplicate gymId is a no-op")
        void addGymAdminFor_duplicate_returnsUnchanged() {
            User withAdmin = baseUser().addGymAdminFor("gym-1");
            User result = withAdmin.addGymAdminFor("gym-1");

            assertThat(result).isSameAs(withAdmin);
            assertThat(result.gymAdminFor()).hasSize(1);
        }

        @Test
        @DisplayName("does not mutate favoriteGyms or routeSetterFor")
        void addGymAdminFor_doesNotAffectOtherLists() {
            User user = baseUser().addFavoriteGym("fav-1").addGymAdminFor("gym-1");

            assertThat(user.favoriteGyms()).containsExactly("fav-1");
            assertThat(user.routeSetterFor()).isEmpty();
        }
    }

    @Nested
    @DisplayName("removeGymAdminFor()")
    class RemoveGymAdminFor {

        @Test
        @DisplayName("removes an existing gymId")
        void removeGymAdminFor_existingGym_removesIt() {
            User user = baseUser().addGymAdminFor("gym-1").removeGymAdminFor("gym-1");

            assertThat(user.gymAdminFor()).isEmpty();
        }

        @Test
        @DisplayName("null gymId is a no-op")
        void removeGymAdminFor_nullGymId_returnsUnchanged() {
            User withAdmin = baseUser().addGymAdminFor("gym-1");
            User result = withAdmin.removeGymAdminFor(null);

            assertThat(result).isSameAs(withAdmin);
        }

        @Test
        @DisplayName("blank gymId is a no-op")
        void removeGymAdminFor_blankGymId_returnsUnchanged() {
            User withAdmin = baseUser().addGymAdminFor("gym-1");
            User result = withAdmin.removeGymAdminFor("  ");

            assertThat(result).isSameAs(withAdmin);
        }

        @Test
        @DisplayName("gymId not present is a no-op")
        void removeGymAdminFor_notPresent_returnsUnchanged() {
            User withAdmin = baseUser().addGymAdminFor("gym-1");
            User result = withAdmin.removeGymAdminFor("gym-99");

            assertThat(result).isSameAs(withAdmin);
        }
    }

    // -------------------------------------------------------------------------
    // addRouteSetterFor / removeRouteSetterFor
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("addRouteSetterFor()")
    class AddRouteSetterFor {

        @Test
        @DisplayName("adds a new gymId to the routeSetterFor list")
        void addRouteSetterFor_emptyList_addsGym() {
            User user = baseUser().addRouteSetterFor("gym-1");

            assertThat(user.routeSetterFor()).containsExactly("gym-1");
        }

        @Test
        @DisplayName("null gymId is a no-op")
        void addRouteSetterFor_nullGymId_returnsUnchanged() {
            User original = baseUser();
            User result = original.addRouteSetterFor(null);

            assertThat(result).isSameAs(original);
        }

        @Test
        @DisplayName("blank gymId is a no-op")
        void addRouteSetterFor_blankGymId_returnsUnchanged() {
            User original = baseUser();
            User result = original.addRouteSetterFor("");

            assertThat(result).isSameAs(original);
        }

        @Test
        @DisplayName("duplicate gymId is a no-op")
        void addRouteSetterFor_duplicate_returnsUnchanged() {
            User withSetter = baseUser().addRouteSetterFor("gym-1");
            User result = withSetter.addRouteSetterFor("gym-1");

            assertThat(result).isSameAs(withSetter);
            assertThat(result.routeSetterFor()).hasSize(1);
        }

        @Test
        @DisplayName("does not mutate favoriteGyms or gymAdminFor")
        void addRouteSetterFor_doesNotAffectOtherLists() {
            User user = baseUser().addFavoriteGym("fav-1").addRouteSetterFor("gym-1");

            assertThat(user.favoriteGyms()).containsExactly("fav-1");
            assertThat(user.gymAdminFor()).isEmpty();
        }
    }

    @Nested
    @DisplayName("removeRouteSetterFor()")
    class RemoveRouteSetterFor {

        @Test
        @DisplayName("removes an existing gymId")
        void removeRouteSetterFor_existingGym_removesIt() {
            User user = baseUser().addRouteSetterFor("gym-1").removeRouteSetterFor("gym-1");

            assertThat(user.routeSetterFor()).isEmpty();
        }

        @Test
        @DisplayName("null gymId is a no-op")
        void removeRouteSetterFor_nullGymId_returnsUnchanged() {
            User withSetter = baseUser().addRouteSetterFor("gym-1");
            User result = withSetter.removeRouteSetterFor(null);

            assertThat(result).isSameAs(withSetter);
        }

        @Test
        @DisplayName("blank gymId is a no-op")
        void removeRouteSetterFor_blankGymId_returnsUnchanged() {
            User withSetter = baseUser().addRouteSetterFor("gym-1");
            User result = withSetter.removeRouteSetterFor("  ");

            assertThat(result).isSameAs(withSetter);
        }

        @Test
        @DisplayName("gymId not present is a no-op")
        void removeRouteSetterFor_notPresent_returnsUnchanged() {
            User withSetter = baseUser().addRouteSetterFor("gym-1");
            User result = withSetter.removeRouteSetterFor("gym-99");

            assertThat(result).isSameAs(withSetter);
        }
    }
}

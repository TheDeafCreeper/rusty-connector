package group.aelysium.rustyconnector.plugin.velocity.lib.family_categories;

import group.aelysium.rustyconnector.plugin.velocity.lib.family.bases.BaseFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.RustyPlayer;

/**
 * A family category is a collection of other families.
 * Categories are only ever allowed to include the same type of family.
 * @param <TFamily> The type of family to assign this category.
 */
public interface FamilyCategory<TFamily extends BaseFamily> {
    /**
     * Connects a player to one of the families in this category.
     * The method by which a family is chosen is strictly reliant on the type of category used.
     * @param player The player to connect.
     */
    void connect(RustyPlayer player) throws Exception;
}

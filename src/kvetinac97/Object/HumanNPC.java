package kvetinac97.Object;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.IntPositionEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPlayerPacket;

public class HumanNPC extends EntityHuman {

    public HumanNPC(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setDataProperty(new FloatEntityData(DATA_SCALE, this.namedTag.getFloat("scale")));
    }

    @Override
    public void spawnTo(Player player) {
        if (!this.hasSpawned.containsKey(player.getLoaderId())) {
            this.hasSpawned.put(player.getLoaderId(), player);

            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, new Player[]{player});

            this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, new Vector3(this.x, this.y, this.z)));
            this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, true);

            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.getUniqueId();
            pk.username = this.getName();
            pk.entityUniqueId = this.getId();
            pk.entityRuntimeId = this.getId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = (float) this.motionX;
            pk.speedY = (float) this.motionY;
            pk.speedZ = (float) this.motionZ;
            pk.yaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.item = Item.get(Item.AIR);
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);

            this.inventory.setHelmet(Item.get(Item.AIR, 0, 1));
            this.inventory.setChestplate(Item.get(Item.AIR, 0, 1));
            this.inventory.setLeggings(Item.get(Item.AIR, 0, 1));
            this.inventory.setBoots(Item.get(Item.AIR, 0, 1));

            this.inventory.sendArmorContents(player);

            this.server.removePlayerListData(this.getUniqueId(), new Player[]{player});

            super.spawnTo(player);
        }
    }

}
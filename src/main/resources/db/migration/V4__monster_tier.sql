-- モンスターバトル：撃破するたびにHPが倍増していく仕組み用
-- monster_tier=何体目のモンスターと戦っているか（1体目=HP3、以降撃破ごとに2倍）
-- monster_damage_accumulated=今戦っている体への累積ダメージ（日をまたいで持ち越す）
ALTER TABLE public.users ADD COLUMN monster_tier integer DEFAULT 1 NOT NULL;
ALTER TABLE public.users ADD COLUMN monster_damage_accumulated integer DEFAULT 0 NOT NULL;

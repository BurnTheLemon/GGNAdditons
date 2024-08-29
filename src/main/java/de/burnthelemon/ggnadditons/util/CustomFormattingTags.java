package de.burnthelemon.ggnadditons.util;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class CustomFormattingTags {
   public static TagResolver getCustomTags() {
      return TagResolver.builder().resolvers(new TagResolver[]{
              Placeholder.parsed("coin", "\ue000"),
              Placeholder.parsed("half_coin", "\ue001"),
              Placeholder.parsed("gray_coin", "\ue002"),
              Placeholder.parsed("ggn", "\ue004"),
              Placeholder.parsed("globalchat", "\ue005"),
              Placeholder.parsed("localchat", "\ue006"),
              Placeholder.parsed("discord", "\ue007"),
              Placeholder.parsed("heart", "\ue008"),
              Placeholder.parsed("wb", "\ue009"),

      }).build();
   }
}

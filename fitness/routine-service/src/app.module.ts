import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { RoutineModule } from './routine/routine.module';

@Module({
  imports: [
    ConfigModule.forRoot({ envFilePath: [`.env`] }),
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: async (configService: ConfigService) => ({
        autoLoadEntities: true,
        type: 'postgres',
        host: configService.get('DB_HOST'),
        port: configService.get('DB_PORT'),
        username: configService.get('DB_USER'),
        password: configService.get('DB_PASSWORD'),
        database: configService.get('DB_DATABASE'),
      }),
    }),
    RoutineModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}

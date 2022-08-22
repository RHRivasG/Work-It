import { ConfigService } from '@nestjs/config';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';

export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(_configService: ConfigService) {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      secretOrKey: _configService.get('JWT_KEY'),
      signOptions: {
        algorithm: 'HS512',
      },
    });
  }

  async validate(payload: any) {
    return { userId: payload.sub, roles: payload.roles };
  }
}

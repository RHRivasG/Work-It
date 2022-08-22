import {
  Body,
  Controller,
  Get,
  Param,
  Post,
  UseGuards,
  Request,
  UnauthorizedException,
  Put,
  Delete,
} from '@nestjs/common';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import {
  RoutineDto,
  RoutineService,
  CreateRoutine,
  UpdateRoutine,
  DeleteRoutine,
} from 'application';

class RoutineCreationForm {
  name: string;
  description: string;
  trainings: string[];
}

class RoutineUpdatingForm {
  name: string;
  description: string;
  trainings: string[];
}

@Controller('routines')
export class RoutineController {
  constructor(private readonly _routineService: RoutineService) {}

  @UseGuards(JwtAuthGuard)
  @Get()
  async getRoutines(@Request() req: any): Promise<RoutineDto[]> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    return await this._routineService.getAll(user.userId);
  }

  @UseGuards(JwtAuthGuard)
  @Get(':id')
  async getRoutine(
    @Param('id') id: string,
    @Request() req: any,
  ): Promise<RoutineDto> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    return await this._routineService.get(
      new Uint8Array(Buffer.from(id, 'hex')),
    );
  }

  @UseGuards(JwtAuthGuard)
  @Post()
  async create(
    @Request() req: any,
    @Body() routine: RoutineCreationForm,
  ): Promise<void> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    const command: CreateRoutine = new CreateRoutine(
      routine.name,
      routine.description,
      user.userId,
      routine.trainings.map(
        (training) => new Uint8Array(Buffer.from(training, 'hex')),
      ),
    );
    command.execute(this._routineService);
  }

  @Put(':id')
  async update(
    @Param('id') id: string,
    @Request() req: any,
    @Body() routine: RoutineUpdatingForm,
  ): Promise<void> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    const command: UpdateRoutine = new UpdateRoutine(
      new Uint8Array(Buffer.from(id, 'hex')),
      routine.name,
      routine.description,
      user.userId,
      routine.trainings.map(
        (training) => new Uint8Array(Buffer.from(training, 'hex')),
      ),
    );
    command.execute(this._routineService);
  }
  @Delete(':id')
  async delete(@Param('id') id: string, @Request() req: any): Promise<void> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    const command: DeleteRoutine = new DeleteRoutine(
      new Uint8Array(Buffer.from(id, 'hex')),
    );
    command.execute(this._routineService);
  }
}
